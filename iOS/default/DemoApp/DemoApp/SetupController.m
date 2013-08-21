#import "SetupController.h"
#import "MenuController.h"
#import "ASIHTTPRequest.h"
#import "DataManager.h"
#import "SVProgressHUD.h"
#import "SVModalWebViewController.h"
#import "RegexKitLite.h"
#import "UINavigationController+Rotation.h"

@interface SetupController ()

- (void) loadPage:(NSString *)name;

- (BOOL) initDownload;

- (void) onSuccess;
- (void) onFail;
- (void) onNetworkError;

@end

@implementation SetupController


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (BOOL)webView:(UIWebView *)wv shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
	
	NSString *requestString = [[request URL] absoluteString];
	NSLog(@"URL is %@", requestString);
	if (requestString == nil) {
		NSLog(@"This is a redirect");
		return YES;
	}
    
    if ([requestString isEqualToString:@"dismiss://"]) {
        [UIView transitionWithView:appDelegate.window duration:0.5 options: UIViewAnimationOptionTransitionFlipFromLeft animations:^{
            
            appDelegate.window.rootViewController = [[UINavigationController alloc] initWithRootViewController:[[MenuController alloc] initWithNibName:@"MenuController" bundle:[NSBundle mainBundle]]];
            
        } completion:nil];
        
        return NO;
    }
    
    if ([requestString isEqualToString:@"loadlogin://"]) {
        [self loadPage:@"login"];
        return NO;
    }
    if ([requestString isEqualToString:@"loadregister://"]) {
        [self loadPage:@"register"];
        return NO;
    }
    if ([requestString isEqualToString:@"loadsignup://"]) {
        [self loadPage:@"signup"];
        return NO;
    }
    
    if ([requestString isEqualToString:@"login://"]) {
        NSString *username = [webView stringByEvaluatingJavaScriptFromString:@"$('#username').val();"];
        NSString *password = [webView stringByEvaluatingJavaScriptFromString:@"$('#password').val();"];
        
        [SVProgressHUD showWithStatus:@"Please wait ..."];
        [DataManager loginUser:username andPassword:password];
        return NO;
    }
    
    if ([requestString isEqualToString:@"register://"]) {
        appDelegate.user = [[User alloc] init];
        appDelegate.user.username = [webView stringByEvaluatingJavaScriptFromString:@"$('#username').val();"];
        appDelegate.user.password = [webView stringByEvaluatingJavaScriptFromString:@"$('#password').val();"];
        appDelegate.user.firstname = [webView stringByEvaluatingJavaScriptFromString:@"$('#firstname').val();"];
        appDelegate.user.lastname = [webView stringByEvaluatingJavaScriptFromString:@"$('#lastname').val();"];
        appDelegate.user.email = [webView stringByEvaluatingJavaScriptFromString:@"$('#email').val();"];
        appDelegate.user.phone = [webView stringByEvaluatingJavaScriptFromString:@"$('#phone').val();"];
        
        [SVProgressHUD showWithStatus:@"Please wait ..."];
        [DataManager registerUser:appDelegate.user];
        return NO;
    }
    
    if ([requestString isEqualToString:@"signup://"]) {
        appDelegate.user = [[User alloc] init];
        appDelegate.user.firstname = [webView stringByEvaluatingJavaScriptFromString:@"$('#firstname').val();"];
        appDelegate.user.lastname = [webView stringByEvaluatingJavaScriptFromString:@"$('#lastname').val();"];
        appDelegate.user.email = [webView stringByEvaluatingJavaScriptFromString:@"$('#email').val();"];
        
        [SVProgressHUD showWithStatus:@"Please wait ..."];
        [DataManager signupUser:appDelegate.user];
        return NO;
    }
        
	
    NSString *regex = @"web\\://(.*)";
    if ([requestString isMatchedByRegex:regex]) {
	    NSString *webAddr = [requestString stringByMatching:regex capture:1L];
        SVModalWebViewController *webViewController = [[SVModalWebViewController alloc] initWithAddress:[NSString stringWithFormat:@"http://%@", webAddr]];
        // [self.navigationController pushViewController:webViewController animated:YES];
        [self presentViewController:webViewController animated:YES completion:^{}];
        return NO;
    }
    
	return YES;
}

- (void)webViewDidFinishLoad:(UIWebView *) wv {
	// [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
	// [SVProgressHUD dismiss];
}

- (void)webViewDidStartLoad:(UIWebView *) wv {
	// [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    // [SVProgressHUD setStatus:@"Loading ..."];
    // [SVProgressHUD show];
}

- (void)webView:(UIWebView *) wv didFailLoadWithError:(NSError *) error {
	[SVProgressHUD dismiss];
    
    int errorCode = [error code];
    // The domain is "NSURLErrorDomain"
    if (errorCode == NSURLErrorNetworkConnectionLost || errorCode == NSURLErrorNotConnectedToInternet || errorCode == NSURLErrorDataNotAllowed || errorCode == NSURLErrorCannotLoadFromNetwork) {
        UIAlertView* alert = [[UIAlertView alloc] init];
        alert.title = @"Network error";
        alert.message = @"Sorry, we can only load this information when there is an active data connection. Please try again later.";
        [alert addButtonWithTitle:@"Dismiss"];
        alert.cancelButtonIndex = 0;
        [alert show];
    }
}



- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onSuccess) name:@"LoginSuccess" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onFail) name:@"LoginFail" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onSuccess) name:@"RegisterSuccess" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onFail) name:@"RegisterFail" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onSuccess) name:@"SignupSuccess" object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onFail) name:@"SignupFail" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNetworkError) name:@"NetworkError" object:nil];
    
    appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    [self loadPage:@"login"];
}

- (void)viewDidUnload {
    [super viewDidUnload];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) loadPage:(NSString *)name {
    NSString *htmlTemplate = [NSString stringWithContentsOfFile:[[NSBundle mainBundle] pathForResource:name ofType:@"html"] encoding:NSUTF8StringEncoding error:nil];
    if (appDelegate.user == nil) {
        htmlTemplate = [htmlTemplate stringByReplacingOccurrencesOfString:@"#{dismiss}" withString:@""];
    } else {
        NSString *dismissStr = @"<p>You are already logged in. Use the Back button below to go back to the app without any change. Use the form below to log in as a different user.</p> <a href=\"dismiss://\" class=\"btn btn-large btn-lima btn-block\">Back</a> <p>&nbsp;</p>";
        htmlTemplate = [htmlTemplate stringByReplacingOccurrencesOfString:@"#{dismiss}" withString:dismissStr];
    }
    [webView loadHTMLString:htmlTemplate baseURL:[NSURL fileURLWithPath:[[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"/html_assets/bootstrap/"]]];
}

- (BOOL) initDownload {
    // Example for single file download
    NSString *localFilePath = [[DataManager docPath] stringByAppendingPathComponent:@"customers-logos1.png"];
    NSURL *url = [NSURL URLWithString:@"http://ringfulhealth.files.wordpress.com/2013/08/customers-logos1.png"];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request setDownloadDestinationPath:localFilePath];
    [request startSynchronous];
    
    int statusCode = -1;
    NSError *error = [request error];
    if (!error) {
        statusCode = [request responseStatusCode];
    }
    
    if (statusCode == 200) {
        return YES;
    } else {
        return NO;
    }
    
    // TODO: add example for multiple file download
}

- (void) onSuccess {
    NSLog(@"onSuccess");
    // [SVProgressHUD dismiss];
    appDelegate.user = [DataManager getUser];
    if (appDelegate.user != nil) {
        // Blocks until it is done
        [SVProgressHUD showWithStatus:@"Downloading content ..."];
        if ([self initDownload]) {
            // Change to menu screen
            [UIView transitionWithView:appDelegate.window duration:0.5 options: UIViewAnimationOptionTransitionFlipFromLeft animations:^{
                
                appDelegate.window.rootViewController = [[UINavigationController alloc] initWithRootViewController:[[MenuController alloc] initWithNibName:@"MenuController" bundle:[NSBundle mainBundle]]];
                
            } completion:nil];
            
            // [DataManager logEvent:@"LOGIN" eventName:@"SUCCESS" episodeId:-1 actionId:-1 answerId:-1 mediaFilename:@"" note:@""];
            
        } else {
            [SVProgressHUD showErrorWithStatus:@"A network error has occurred. Please try again later."];
        }
    } else {
        [SVProgressHUD showErrorWithStatus:@"An error has occurred. Please try again later."];
    }
}

- (void) onFail {
    // remove current user
    [DataManager removeUser:appDelegate.user.dbId];
    appDelegate.user = nil;
    
    [SVProgressHUD showErrorWithStatus:@"Login failed. Please make sure that you have the right username and password."];
    
    // [DataManager logEvent:@"LOGIN" eventName:@"FAIL" episodeId:-1 actionId:-1 answerId:-1 mediaFilename:@"" note:@""];
}

- (void) onNetworkError {
    appDelegate.user = nil;
    [SVProgressHUD showErrorWithStatus:@"Network error. Please make sure that you have an active Internet connection on this device."];
}

- (BOOL)shouldAutorotate {
    return YES;
}
- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskAll;
}
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return YES;
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
