#import <UIKit/UIKit.h>
#import "User.h"

#define APPDOMAIN @"demoapp.ringfulhealth.com"

@interface AppDelegate : UIResponder <UIApplicationDelegate> {
    User *user;
}

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) User *user;

void uncaughtExceptionHandler(NSException *exception);

@end
