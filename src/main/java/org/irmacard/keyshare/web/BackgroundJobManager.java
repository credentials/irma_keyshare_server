package org.irmacard.keyshare.web;

import org.irmacard.keyshare.web.email.EmailSender;
import org.irmacard.keyshare.web.email.EmailVerificationRecord;
import org.irmacard.keyshare.web.users.User;
import org.irmacard.keyshare.web.users.Users;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class BackgroundJobManager implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(BackgroundJobManager.class);
	static private ScheduledExecutorService scheduler;

    public static ScheduledExecutorService getScheduler() {
        if (scheduler == null) {
            synchronized (BackgroundJobManager.class) {
                if (scheduler == null) {
                    scheduler = Executors.newScheduledThreadPool(2);
                }
            }
        }
        return scheduler;
    }

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Setting up database cleanup and inactive user removal cron tasks");
        ScheduledExecutorService sched = getScheduler();

		sched.scheduleAtFixedRate(new Runnable() {
			@Override public void run() {
				try {
					logger.warn("Deleting expired email verifications");
					KeyshareApplication.openDatabase();
					EmailVerificationRecord.delete(
							"(time_verified IS NULL AND time_created + timeout < ?) "
									+ "OR (time_verified IS NOT NULL AND time_verified + validity < ?)",
							System.currentTimeMillis() / 1000,
							System.currentTimeMillis() / 1000
					);
				} catch (Exception e) {
					logger.error("Failed to run database cleanup cron task:");
					e.printStackTrace();
				}
			}
		}, 6, 6, TimeUnit.HOURS);

		if (!KeyshareConfiguration.getInstance().getUnregisterExpiredUsers())
			return;

		sched.scheduleAtFixedRate(new Runnable() {
			@Override public void run() {
				try {
					logger.warn("Checking for inactive users");
					KeyshareApplication.openDatabase();

					// Warn users that have been inactive for too long that their account will be deleted if they remain inactive
					for (Map row : inactiveUsers(false)) {
						try {
							String username = (String) row.get("username");
							String email = (String) row.get("emailAddress");
							String lang = (String) row.get("language");
							System.out.printf("Sending %s expiry warning to %s at %s\n", lang, username, email);
							EmailSender.send(
									email,
									KeyshareConfiguration.getInstance().getExpireEmailSubject(lang),
									KeyshareConfiguration.getInstance().getExpireEmailBody(lang),
									true,
									username,
									email
							);
							// Save for later runs that we warned the user at this time
							User user = Users.getUser(username);
							user.setLong(User.EXPIRY_WARNING_FIELD, System.currentTimeMillis() / 1000);
							user.saveIt();
						} catch (Exception ex) {
							logger.error(ex.getMessage());
							ex.printStackTrace();
						}
					}

					// Delete user accounts of users that have been inactive and warned about it
					for (Map row : inactiveUsers(true)) {
						try {
							String username = (String) row.get("username");
							System.out.println("Deleting expired user " + username);
							Users.getUser(username).unregister();
						} catch (Exception ex) {
							logger.error(ex.getMessage());
							ex.printStackTrace();
						}
					}

				} catch(Exception ex) {
					logger.error("Failed to check for inactive users: " + ex.getMessage());
					ex.printStackTrace();
				} finally {
					KeyshareApplication.closeDatabase();
				}
			}
		}, 0, 6, TimeUnit.HOURS);
	}

	private final static int expiryMonths = 12;
	private final static int expiryWaitDays = 14;

	private static List<Map> inactiveUsers(boolean warned) {
		String whereClause;
		long arg;
		Calendar cal = Calendar.getInstance();

		if (warned) {
			cal.add(Calendar.DAY_OF_MONTH, -expiryWaitDays);
			arg = cal.getTimeInMillis()/1000;
			whereClause = "where expiryWarning > lastActive and expiryWarning < ?";
		} else {
			cal.add(Calendar.MONTH, -expiryMonths);
			arg = cal.getTimeInMillis()/1000;
			whereClause = "where lastActive < ? and (expiryWarning is null or expiryWarning < lastActive) " +
					"limit 20"; // Ensure we don't send out too many emails at once
		}

		// In the inner query, we select all dates at which any user did anything, in the app or webinterface
		// In the middle query, we group them together, taking the latest date
		// In the outer query we fetch the user's email addresses, and filter.
		String query = "select users.username, email_addresses.emailAddress, users.language from (\n" +
				"  select id, max(lastActive) as lastActive from (\n" +
				"    select log_entry_records.user_id as id, log_entry_records.time as lastActive\n" +
				"    from log_entry_records\n" +
				"    union\n" +
				"    select users.id, lastSeen as lastActive\n" +
				"    from users\n" +
				"    where users.lastSeen is not null\n" +
				"  ) i group by id\n" +
				") o inner join users on o.id = users.id\n" +
				"inner join email_addresses on users.id = email_addresses.user_id\n" +
				whereClause;

		logger.info("Searching for users since " + arg);

		return Base.findAll(query, arg);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		getScheduler().shutdownNow();
	}
}
