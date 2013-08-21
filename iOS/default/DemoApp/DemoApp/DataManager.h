#import <Foundation/Foundation.h>
#import "User.h"

@interface DataManager : NSObject

+ (void) init;
+ (void) close;

+ (NSString *) formattedDate:(NSDate *)date;
+ (NSString *) formattedTime:(NSDate *)date;
+ (NSString *) formattedDateTime:(NSDate *)date;
+ (NSString *) html5DateTime:(NSDate *)date;
+ (NSString *) elapsedTime:(NSDate *)date;

+ (NSDate *) dateTimeFromHtml5:(NSString *)s;

+ (void) logEvent:(NSString *)eventName withParam:(NSString *)param andMachParam:(NSString *)mparam;
+ (BOOL) removeLogEvent: (int) event_id;
+ (void) clearLogEvents;

+ (BOOL) saveUser: (User *) user;
+ (User *) getUser;
+ (BOOL) removeUser: (int) user_id;
+ (void) loginUser:(NSString *)un andPassword:(NSString *)pass;
+ (void) registerUser:(User *) user;
+ (void) signupUser:(User *) user; // do not use username or password, just sends the user to server and save

+ (NSString *) docPath;

@end
