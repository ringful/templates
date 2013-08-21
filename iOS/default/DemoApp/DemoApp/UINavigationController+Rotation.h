//
//  UINavigationController+Rotation.h
//  Hcahps
//
//  Created by Michael Yuan on 10/16/12.
//  Copyright (c) 2012 Ringful LLC. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UINavigationController (Rotation)

-(BOOL)shouldAutorotate;
-(NSUInteger)supportedInterfaceOrientations;
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation;

@end
