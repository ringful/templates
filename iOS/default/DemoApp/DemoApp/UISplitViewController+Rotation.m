//
//  UISplitViewController+Rotation.m
//  Ratings
//
//  Created by Michael Yuan on 10/20/12.
//  Copyright (c) 2012 Ringful LLC. All rights reserved.
//

#import "UISplitViewController+Rotation.h"

@implementation UISplitViewController (Rotation)

-(BOOL)shouldAutorotate {
    return [[self.viewControllers lastObject] shouldAutorotate];
}

-(NSUInteger)supportedInterfaceOrientations {
    return [[self.viewControllers lastObject] supportedInterfaceOrientations];
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return [[self.viewControllers lastObject] preferredInterfaceOrientationForPresentation];
}

@end
