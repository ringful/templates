#import "MenuController.h"
#import "AppDelegate.h"
#import "SetupController.h"

@interface MenuController ()

@end

@implementation MenuController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (IBAction)changeUser:(id)sender {
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
    
    [UIView transitionWithView:appDelegate.window duration:0.5 options: UIViewAnimationOptionTransitionFlipFromLeft animations:^{
        
        appDelegate.window.rootViewController = [[UINavigationController alloc] initWithRootViewController:[[SetupController alloc] initWithNibName:@"SetupController" bundle:[NSBundle mainBundle]]];
        
    } completion:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
