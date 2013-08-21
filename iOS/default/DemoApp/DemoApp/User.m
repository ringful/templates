#import "User.h"

@implementation User

@synthesize dbId, username, password, firstname, lastname, email, phone;

- (id) init {
    self.dbId = 0;
    
    self.username = @"";
    self.password = @"";
    
    self.firstname = @"";
    self.lastname = @"";
    
    self.email = @"";
    self.phone = @"";

    return self;
}

+ (User *) readDataFrom:(FMResultSet *) rs {
    User *user = [[User alloc] init];
	
	user.dbId = [rs intForColumn:@"id"];
    
	user.username = [rs stringForColumn:@"username"];
    user.password = [rs stringForColumn:@"password"];
    
    user.firstname = [rs stringForColumn:@"firstname"];
    user.lastname = [rs stringForColumn:@"lastname"];
    
    user.email = [rs stringForColumn:@"email"];
    user.phone = [rs stringForColumn:@"phone"];
    
    return user;
}


@end
