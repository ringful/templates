#import "AppDelegate.h"
#import "DataManager.h"
#import "UINavigationController+Rotation.h"
#import "TestFlight.h"
#import "SetupController.h"
#import "MenuController.h"

@implementation AppDelegate

@synthesize user;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    NSSetUncaughtExceptionHandler(&uncaughtExceptionHandler);
    // [TestFlight takeOff:@"11e5f438-57bb-4bd2-babe-473419495462"];
    // [FlurryAnalytics startSession:@"WDXRNY3S8MSWYD3NHCT4"];
    
    NSString *dbFilePath = [[DataManager docPath] stringByAppendingPathComponent:@"logging.db"];
	if (![[NSFileManager defaultManager] fileExistsAtPath:dbFilePath]) {
        NSString *backupDbPath = [[NSBundle mainBundle] pathForResource:@"logging" ofType:@"db"];
		if (backupDbPath == nil) {
			NSLog (@"couldnt find logging db to copy, bail");
		} else {
			BOOL copiedBackupDb = [[NSFileManager defaultManager] copyItemAtPath:backupDbPath toPath:dbFilePath error:nil];
			if (!copiedBackupDb) {
				NSLog (@"copying logging db failed, bail");
			}
		}
	} else {
		NSLog (@"found logging.db in the app directory");
	}
    dbFilePath = [[DataManager docPath] stringByAppendingPathComponent:@"user.db"];
	if (![[NSFileManager defaultManager] fileExistsAtPath:dbFilePath]) {
        NSString *backupDbPath = [[NSBundle mainBundle] pathForResource:@"user" ofType:@"db"];
		if (backupDbPath == nil) {
			NSLog (@"couldnt find user db to copy, bail");
		} else {
			BOOL copiedBackupDb = [[NSFileManager defaultManager] copyItemAtPath:backupDbPath toPath:dbFilePath error:nil];
			if (!copiedBackupDb) {
				NSLog (@"copying user db failed, bail");
			}
		}
	} else {
		NSLog (@"found user.db in the app directory");
	}
    // The episdoes.db file may NOT have been downloaded at this point.
    // We will initialize the episodes database at MenuController
    [DataManager init];
    self.user = [DataManager getUser];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    // self.window.backgroundColor = [UIColor whiteColor];
    
    if (user == nil) {
        self.window.rootViewController = [[SetupController alloc] initWithNibName:@"SetupController" bundle:[NSBundle mainBundle]];
    } else {
        // sync happens in "becomeActive"
        self.window.rootViewController = [[UINavigationController alloc] initWithRootViewController:[[MenuController alloc] initWithNibName:@"MenuController" bundle:[NSBundle mainBundle]]];
    }
    
    [self.window makeKeyAndVisible];
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    [DataManager clearLogEvents];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

void uncaughtExceptionHandler(NSException *exception) {
    // [FlurryAnalytics logError:@"Uncaught" message:@"Crash!" exception:exception];
}


@end
