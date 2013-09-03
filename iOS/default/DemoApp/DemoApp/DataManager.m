#import "DataManager.h"
#import "FMDatabase.h"
#import "FMDatabaseAdditions.h"
#import "AppDelegate.h"
#import "SBJson.h"
#import "OpenUDID.h"
#import "ASIHTTPRequest.h"
#import "ASIFormDataRequest.h"
#import "ASINetworkQueue.h"

static FMDatabase *logging_db = nil;
static FMDatabase *user_db = nil;

@implementation DataManager

+ (void) init {
    [self close]; // close first
    
    NSString *logging_dbFilePath = [[DataManager docPath] stringByAppendingPathComponent:@"logging.db"];
    logging_db = [FMDatabase databaseWithPath:logging_dbFilePath];
    if (![logging_db open]) {
        NSLog(@"Could not open logging_db");
    }
    
    NSString *user_dbFilePath = [[DataManager docPath] stringByAppendingPathComponent:@"user.db"];
    user_db = [FMDatabase databaseWithPath:user_dbFilePath];
    if (![user_db open]) {
        NSLog(@"Could not open user_db");
    }
}

+ (void) close {
    if (logging_db != nil) {
        [logging_db close];
        logging_db = nil;
    }
    if (user_db != nil) {
        [user_db close];
        user_db = nil;
    }
}


+ (NSString *) formattedDate:(NSDate *)date {
    if (date == nil || [date timeIntervalSince1970] == 0.0) {
        return @"";
    }
    
    NSLocale *locale = [NSLocale currentLocale];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    NSString *dateFormat = [NSDateFormatter dateFormatFromTemplate:@"E MMM d yyyy" options:0 locale:locale];
    [formatter setDateFormat:dateFormat];
    [formatter setLocale:locale];
    return [formatter stringFromDate:date];
}

+ (NSString *) formattedTime:(NSDate *)date {
    if (date == nil || [date timeIntervalSince1970] == 0.0) {
        return @"";
    }
    
    NSLocale *locale = [NSLocale currentLocale];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    NSString *dateFormat = [NSDateFormatter dateFormatFromTemplate:@"hh:mm:ss" options:0 locale:locale];
    [formatter setDateFormat:dateFormat];
    [formatter setLocale:locale];
    return [formatter stringFromDate:date];
}

+ (NSString *) formattedDateTime:(NSDate *)date {
    if (date == nil || [date timeIntervalSince1970] == 0.0) {
        return @"";
    }
    
    NSLocale *locale = [NSLocale currentLocale];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    NSString *dateFormat = [NSDateFormatter dateFormatFromTemplate:@"E MMM d yyyy hh:mm:ss " options:0 locale:locale];
    [formatter setDateFormat:dateFormat];
    [formatter setLocale:locale];
    return [formatter stringFromDate:date];
}

+ (NSString *) html5DateTime:(NSDate *)date {
    if (date == nil || [date timeIntervalSince1970] == 0.0) {
        return @"";
    }
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss'Z'"];
    return [dateFormatter stringFromDate:date];
    /*
    NSLocale *locale = [NSLocale currentLocale];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    NSString *dateFormat = [NSDateFormatter dateFormatFromTemplate:@"yyyy-MM-dd'T'HH:mm:ss" options:0 locale:locale];
    [formatter setDateFormat:dateFormat];
    [formatter setLocale:locale];
    return [formatter stringFromDate:date];
    */
}

+ (NSDate *) dateTimeFromHtml5:(NSString *)s {
    NSLog(@"html5 string submitted %@", s);
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    NSDate *result = nil;
    
    [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss.S'Z'"];
    result = [dateFormatter dateFromString:s];
    if (result != nil) {
        return result;
    }
    
    [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss'Z'"];
    result = [dateFormatter dateFromString:s];
    if (result != nil) {
        return result;
    }
    
    [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm'Z'"];
    result = [dateFormatter dateFromString:s];
    if (result != nil) {
        return result;
    }
    
    return nil;
}

+ (NSString *) elapsedTime:(NSDate *)date {
    if (date == nil || [date timeIntervalSince1970] == 0.0) {
        return @"No Date";
    }
    
    NSUInteger desiredComponents = NSYearCalendarUnit | NSMonthCalendarUnit | NSWeekCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit
    | NSMinuteCalendarUnit | NSSecondCalendarUnit;
    
    NSDateComponents *elapsedTimeUnits = [[NSCalendar currentCalendar] components:desiredComponents fromDate:date toDate:[NSDate date] options:0];
    
    NSString *format = @"%d %@ ago";
    NSInteger number = 0;
    NSString *unit;
    
    if ([elapsedTimeUnits year] > 0) {
        number = [elapsedTimeUnits year];
        unit = [NSString stringWithFormat:@"year"];
    }
    else if ([elapsedTimeUnits month] > 0) {
        number = [elapsedTimeUnits month];
        unit = [NSString stringWithFormat:@"month"];
    }
    else if ([elapsedTimeUnits week] > 0) {
        number = [elapsedTimeUnits week];
        unit = [NSString stringWithFormat:@"week"];
    }
    else if ([elapsedTimeUnits day] > 0) {
        number = [elapsedTimeUnits day];
        unit = [NSString stringWithFormat:@"day"];
    }
    else if ([elapsedTimeUnits hour] > 0) {
        number = [elapsedTimeUnits hour];
        unit = [NSString stringWithFormat:@"hour"];
    }
    else if ([elapsedTimeUnits minute] > 0) {
        number = [elapsedTimeUnits minute];
        unit = [NSString stringWithFormat:@"minute"];
    }
    else if ([elapsedTimeUnits second] > 0) {
        number = [elapsedTimeUnits second];
        unit = [NSString stringWithFormat:@"second"];
    }
    // check if unit number is greater then append s at the end
    if (number > 1) {
        unit = [NSString stringWithFormat:@"%@s", unit];
    }
    return [NSString stringWithFormat:format, number, unit];
}


+ (NSString *) docPath {
    return [NSHomeDirectory() stringByAppendingPathComponent:@"Documents/"];
}

+ (void) logEvent:(NSString *)eventName withParam:(NSString *)param andMachParam:(NSString *)mparam {
    
    NSDate *now = [NSDate date];
    
    // Create DB entry
    NSString *sql = @"INSERT INTO logs (eventName, eventDate, param, mparam) VALUES (?, ?, ?, ?)";
    [logging_db executeUpdate:sql, eventName, [NSNumber numberWithDouble:[now timeIntervalSince1970]], param, mparam];
    int dbId = -1;
    FMResultSet *rs = [logging_db executeQuery:@"SELECT last_insert_rowid()"];
    if ([rs next]) {
        dbId = [rs intForColumnIndex:0];
    } else {
        NSLog(@"ERROR: the log event %@ is not saved!", eventName);
    }
    [rs close];
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/event_log", APPDOMAIN]];
    ASIFormDataRequest *_request = [ASIFormDataRequest requestWithURL:url];
    __weak ASIFormDataRequest *request = _request;
    [request setShouldContinueWhenAppEntersBackground:YES];
    
    [request setRequestMethod:@"POST"];
    [request addPostValue:@"DemoAppIOS" forKey:@"appName"];
    [request addPostValue:[OpenUDID value] forKey:@"appUserId"];
    [request addPostValue:eventName forKey:@"eventName"];
    [request addPostValue:[NSString stringWithFormat:@"%lld", (long long) ([now timeIntervalSince1970] * 1000l)] forKey:@"eventDate"];
    [request addPostValue:param forKey:@"eventParam"];
    [request addPostValue:mparam forKey:@"eventMachParam"];
    
    NSLog(@"DBID is %d", dbId);
    [request setCompletionBlock:^{
        NSLog(@"CompletionBlock is called");
        // Use when fetching text data
        // NSString *responseString = [request responseString];
        // Use when fetching binary data
        // NSData *responseData = [request responseData];
        
        int statusCode = [request responseStatusCode];
        if (statusCode == 200) {
            NSLog (@"Finished loading data for log event %d", dbId);
            [DataManager removeLogEvent:dbId];
        } else {
            NSLog (@"Received error with HTTP status code %d, log event %d is NOT deleted", statusCode, dbId);
        }
    }];
    
    [request startAsynchronous];
    
    /*
    LogEventNetworkDelegate *nd = [[LogEventNetworkDelegate alloc] init];
    nd.dbId = dbId;
    [request setDelegate:nd]; 
    // PROBLEM: The request does not retain the delegate
    // SOLUTION: Make nd a static object, and use "tag" for dbId
    [request startAsynchronous];
    */
    
    /*
    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:url];
    [req setHTTPMethod:@"POST"];
    [req setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    NSMutableData *postBody = [NSMutableData data];
    [postBody appendData:[@"appName=LifeLineIOS" dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[[NSString stringWithFormat:@"&appUserId=%@", [HKMOpenUDID value]] dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[[NSString stringWithFormat:@"&eventName=%@", eventName] dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[[NSString stringWithFormat:@"&eventDate=%lld", (long long) ([now timeIntervalSince1970] * 1000l)] dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[[NSString stringWithFormat:@"&eventParam=%@", param] dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[[NSString stringWithFormat:@"&eventMachParam=%@", mparam] dataUsingEncoding:NSUTF8StringEncoding]];
    [req setHTTPBody:postBody];
    
    [[NSURLConnection alloc] initWithRequest:req delegate:nd];
    */
}

+ (BOOL) removeLogEvent: (int) event_id {
    NSString *sql = @"DELETE FROM logs WHERE id=?";
	[logging_db executeUpdate:sql, [NSNumber numberWithInt:event_id]];
	return YES;
}

+ (void) clearLogEvents {
    ASINetworkQueue *q = [[ASINetworkQueue alloc] init];
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/event_log", APPDOMAIN]];
    
    NSString *sql = @"SELECT * FROM logs";
	FMResultSet *rs = [logging_db executeQuery:sql];
    while ([rs next]) {
		ASIFormDataRequest *_request = [ASIFormDataRequest requestWithURL:url];
        __weak ASIFormDataRequest *request = _request;
        [request setShouldContinueWhenAppEntersBackground:YES];
        
        [request setRequestMethod:@"POST"];
        [request addPostValue:@"LifeLineIOS" forKey:@"appName"];
        [request addPostValue:[OpenUDID value] forKey:@"appUserId"];
        [request addPostValue:[rs stringForColumn:@"eventName"] forKey:@"eventName"];
        [request addPostValue:[NSString stringWithFormat:@"%lld", (long long) ([rs doubleForColumn:@"eventDate"] * 1000l)] forKey:@"eventDate"];
        [request addPostValue:[rs stringForColumn:@"param"] forKey:@"eventParam"];
        [request addPostValue:[rs stringForColumn:@"mparam"] forKey:@"eventMachParam"];
        
        /*
        LogEventNetworkDelegate *nd = [[LogEventNetworkDelegate alloc] init];
        nd.dbId = [rs intForColumn:@"id"];
        [request setDelegate:nd];
        */
        
        int dbId = [rs intForColumn:@"id"];
        [request setCompletionBlock:^{
            int statusCode = [request responseStatusCode];
            if (statusCode == 200) {
                NSLog (@"Finished loading data for log event %d", dbId);
                [DataManager removeLogEvent:dbId];
            } else {
                NSLog (@"Received error with HTTP status code %d, log event %d is NOT deleted", statusCode, dbId);
            }
        }];
        
        [q addOperation:request];
	}
	[rs close];
    
    [q go];
}


+ (BOOL) saveUser: (User *) user {
    BOOL inserted = NO;
	if (user.dbId == 0) {
        user.dbId = 1;
        NSString *sql = @"INSERT INTO user (id, username, password, firstname, lastname, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
		[user_db executeUpdate:sql,
         [NSNumber numberWithInt:user.dbId],
         user.username,
         user.password,
         user.firstname,
         user.lastname,
         user.email,
         user.phone
        ];
		inserted = YES;
        
        FMResultSet *rs = [user_db executeQuery:@"SELECT last_insert_rowid()"];
		if ([rs next]) {
			int lastId = [rs intForColumnIndex:0];
            NSLog(@"lastId is %d", lastId);
		}
		[rs close];
		
	} else {
        user.dbId = 1;
        NSString *sql = @"UPDATE user SET username=?, password=?, firstname=?, lastname=?, email=?, phone=? WHERE id=1";
		[user_db executeUpdate:sql,
         user.username,
         user.password,
         user.firstname,
         user.lastname,
         user.email,
         user.phone
        ];
		inserted = NO;
	}
	
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
    appDelegate.user = user;
    
	return inserted;
}

+ (User *) getUser {
    User *user = nil;
    NSString *sql = @"SELECT * FROM user where id=1";
	FMResultSet *rs = [user_db executeQuery:sql];
    if ([rs next]) {
		user = [User readDataFrom:rs];
	}
	[rs close];
	
	return user;
}

+ (BOOL) removeUser: (int) user_id {
    NSString *sql = @"DELETE FROM user WHERE id=?";
	[user_db executeUpdate:sql, [NSNumber numberWithInt:user_id]];
	return YES;
}

+ (void) loginUser:(NSString *)un andPassword:(NSString *)pass {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/mlogin", APPDOMAIN]];;
    ASIFormDataRequest *_request = [ASIFormDataRequest requestWithURL:url];
    __weak ASIFormDataRequest *request = _request;
    [request setShouldContinueWhenAppEntersBackground:YES];
    
    [request setRequestMethod:@"POST"];
    [request addPostValue:un forKey:@"username"];
    [request addPostValue:pass forKey:@"password"];
    
    [request setFailedBlock:^{
        NSError *error = [request error];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"NetworkError" object:error];
    }];
    
    [request setCompletionBlock:^{
        int statusCode = [request responseStatusCode];
        if (statusCode == 200) {
            NSLog (@"LoginUser: Finished loading");
            
            NSString *responseString = [request responseString];
            NSLog(@"RESPONSE IS %@", responseString);
            SBJsonParser *parser = [[SBJsonParser alloc] init];
            NSDictionary *data = [parser objectWithString:responseString];
            if (data != nil && [@"ok" isEqualToString:[data objectForKey:@"result"]]) {
                
                // The user might already exist
                User *user = [DataManager getUser];
                if (user == nil) {
                    user = [[User alloc] init];
                }
                user.username = un;
                user.password = pass;
                user.firstname = [data objectForKey:@"firstname"];
                user.lastname = [data objectForKey:@"lastname"];
                user.email = [data objectForKey:@"email"];
                [DataManager saveUser:user];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:@"LoginSuccess" object:nil];
                
            } else if (data != nil && [@"error" isEqualToString:[data objectForKey:@"result"]]) {
                [[NSNotificationCenter defaultCenter] postNotificationName:@"LoginFail" object:nil];
                
            } else {
                [[NSNotificationCenter defaultCenter] postNotificationName:@"LoginFail" object:nil];
            }
            
        } else {
            NSLog (@"loginUser: Received error with HTTP status code %d", statusCode);
            [[NSNotificationCenter defaultCenter] postNotificationName:@"LoginFail" object:nil];
        }
    }];
    
    [request startAsynchronous];
}

+ (void) registerUser:(User *)user {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/mregister", APPDOMAIN]];
    ASIFormDataRequest *_request = [ASIFormDataRequest requestWithURL:url];
    __weak ASIFormDataRequest *request = _request;
    [request setShouldContinueWhenAppEntersBackground:YES];
    
    [request setRequestMethod:@"POST"];
    [request addPostValue:user.username forKey:@"username"];
    [request addPostValue:user.password forKey:@"password"];
    [request addPostValue:user.firstname forKey:@"firstname"];
    [request addPostValue:user.lastname forKey:@"lastname"];
    [request addPostValue:user.email forKey:@"email"];
    [request addPostValue:user.phone forKey:@"phone"];
    
    [request setFailedBlock:^{
        NSError *error = [request error];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"NetworkError" object:error];
    }];
    
    [request setCompletionBlock:^{
        int statusCode = [request responseStatusCode];
        if (statusCode == 200) {
            NSLog (@"RegisterUser: Finished loading");
            
            NSString *responseString = [request responseString];
            NSLog(@"RESPONSE IS %@", responseString);
            SBJsonParser *parser = [[SBJsonParser alloc] init];
            NSDictionary *data = [parser objectWithString:responseString];
            if (data != nil && [@"ok" isEqualToString:[data objectForKey:@"result"]]) {
                
                User *exist = [DataManager getUser];
                if (exist) {
                    user.dbId = 1;
                }
                [DataManager saveUser:user];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:@"RegisterSuccess" object:nil];
                
            } else if (data != nil && [@"error" isEqualToString:[data objectForKey:@"result"]]) {
                [[NSNotificationCenter defaultCenter] postNotificationName:@"RegisterFail" object:nil];
                
            } else {
                [[NSNotificationCenter defaultCenter] postNotificationName:@"RegisterFail" object:nil];
            }
            
        } else {
            NSLog (@"registerUser: Received error with HTTP status code %d", statusCode);
            [[NSNotificationCenter defaultCenter] postNotificationName:@"RegisterFail" object:nil];
        }
    }];
    
    [request startAsynchronous];
}

+ (void) signupUser:(User *)user {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/msignup", APPDOMAIN]];;
    ASIFormDataRequest *_request = [ASIFormDataRequest requestWithURL:url];
    __weak ASIFormDataRequest *request = _request;
    [request setShouldContinueWhenAppEntersBackground:YES];
    
    [request setRequestMethod:@"POST"];
    [request addPostValue:user.firstname forKey:@"firstname"];
    [request addPostValue:user.lastname forKey:@"lastname"];
    [request addPostValue:user.email forKey:@"email"];
    
    [request setFailedBlock:^{
        NSError *error = [request error];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"NetworkError" object:error];
    }];
    
    [request setCompletionBlock:^{
        int statusCode = [request responseStatusCode];
        if (statusCode == 200) {
            NSLog (@"SignupUser: Finished loading");
            
            NSString *responseString = [request responseString];
            NSLog(@"RESPONSE IS %@", responseString);
            SBJsonParser *parser = [[SBJsonParser alloc] init];
            NSDictionary *data = [parser objectWithString:responseString];
            if (data != nil && [@"ok" isEqualToString:[data objectForKey:@"result"]]) {
                
                User *exist = [DataManager getUser];
                if (exist) {
                    user.dbId = 1;
                }
                [DataManager saveUser:user];
                
                AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
                appDelegate.user = [DataManager getUser];
                
                [[NSNotificationCenter defaultCenter] postNotificationName:@"SignupSuccess" object:nil];
                
            } else if (data != nil && [@"error" isEqualToString:[data objectForKey:@"result"]]) {
                [[NSNotificationCenter defaultCenter] postNotificationName:@"SignupFail" object:nil];
                
            } else {
                [[NSNotificationCenter defaultCenter] postNotificationName:@"SignupFail" object:nil];
            }
            
        } else {
            NSLog (@"signupUser: Received error with HTTP status code %d", statusCode);
            [[NSNotificationCenter defaultCenter] postNotificationName:@"SignupFail" object:nil];
        }
    }];
    
    [request startAsynchronous];
}


@end
