#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface CDVMulticast : CDVPlugin

// - (void)execute:(CDVInvokedUrlCommand *)command;
- (void)create:(CDVInvokedUrlCommand *)command;
- (void)joinGroup:(CDVInvokedUrlCommand *)command;
- (void)bind:(CDVInvokedUrlCommand *)command;

@end