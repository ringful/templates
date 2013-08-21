//
//  UISplitViewController+Rotation.h
//  Ratings
//
//  Created by Michael Yuan on 10/20/12.
//  Copyright (c) 2012 Ringful LLC. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UISplitViewController (Rotation)

- (BOOL)shouldAutorotate;
- (NSUInteger)supportedInterfaceOrientations;
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation;

@end
