#import <Foundation/Foundation.h>
#import "FMDatabase.h"
#import "FMDatabaseAdditions.h"

@interface User : NSObject {
    int dbId;
    
    NSString *username;
    NSString *password;
    
    NSString *firstname;
    NSString *lastname;
    NSString *email;
    NSString *phone;
}

@property (nonatomic) int dbId;

@property (strong, nonatomic) NSString *username;
@property (strong, nonatomic) NSString *password;

@property (strong, nonatomic) NSString *firstname;
@property (strong, nonatomic) NSString *lastname;
@property (strong, nonatomic) NSString *email;
@property (strong, nonatomic) NSString *phone;

- (id) init;
+ (User *) readDataFrom:(FMResultSet *) rs;


@end