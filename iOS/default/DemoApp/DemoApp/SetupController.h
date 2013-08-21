#import <UIKit/UIKit.h>
#import "AppDelegate.h"

@interface SetupController : UIViewController <UIWebViewDelegate> {
    IBOutlet UIWebView *webView;
    
    AppDelegate *appDelegate;
}

@end
