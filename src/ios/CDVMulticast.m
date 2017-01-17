#import <Cordova/CDV.h>
#import "CDVMulticast.h"

@interface CDVMulticast () {}
@end

@implementation CDVMulticast

// MulticastClient* multicastClient;

/*
- (void)pluginInitialize
{
    //setup multicast client
    multicastClient = [[MulticastClient alloc] init];

    while(1)
    {
        bool connectedStream = false;
        
        sleep(1);
        
        //State for managing multicast connections
        //This is reached on start up, every time the user presses refresh, and every time the user hits "view live"
        //Its called alot to make sure there is good service. Multicast is connectionless, its hard to know if you arn't receiving packets becasue none are there or there there is problem
        if(mode == CONNECT_MULTI)
        {
            //are we connected? try to close it
            if([multicastClient isSocketOpen])
            {
                int socketCount = 0;
                [multicastClient closeSocket];
                
                //try to close it nicely 5 times
                while([multicastClient isSocketOpen] &&  socketCount<5 )
                {
                    sleep(1.0);
                    NSLog(@"Waiting on Socket close");
                    socketCount++;
                }
            }
            
            //reinti client. will force close connection if we havnt yet
            multicastClient = [[MulticastClient alloc] init];
            
            int cnt = 1;
            NSString* ssidTemp = SSID;
            
            //Connect multicast if we are not connected yet and we are on the appropriate wifi network
            while((!connectedStream) && cnt<=5 && [[NetworkCheck whatIsMySSID] isEqualToString:ssidTemp])
            {
                NSLog(@"Trying to connect... %d",cnt);
                cnt++;
                
                //init multicast connection
                connectedStream = [multicastClient startMulticastListenerOnPort:kPortNumber withAddress: [NSString stringWithUTF8String:kMulticastAddress]];
                if(!connectedStream)
                {
                    sleep(1.0);
                }
                
            }
            connectionLoop++;
            
            //If we connected and are on the right network start client listen loop
            if(connectedStream && [[NetworkCheck whatIsMySSID] isEqualToString:ssidTemp])
            {
                [multicastClient startListen];
                appDelegate.splashViewController.onlineMode = true;
                
                //Wait for all alerts to disappear
                while(appDelegate.splashViewController.alertsPresent)
                {
                    NSLog(@"Waiting for initial connection alerts to dissapear");
                    sleep(1.0);
                }
                if(connectionLoop == 1)
                {
                    //On first connection send a welcome message and a connection success
                    [self performSelectorOnMainThread:@selector(sendConnectionSuccessMessage) withObject:nil waitUntilDone:false];
                }
                else{
                    //All other times, send a simple "connected!" message.
                    [self performSelectorOnMainThread:@selector(sendSecondaryConnectionSuccessMessage) withObject:nil waitUntilDone:false];
                    
                }
                
            }
            //send a connection failure message
            else{
                //wait for initial alerts to dissapear
                while(appDelegate.splashViewController.alertsPresent)
                {
                    NSLog(@"Waiting for initial connection alerts to dissapear");
                    sleep(2);
                }
                
                if(connectionLoop > 1)
                {
                    appDelegate.splashViewController.onlineMode = false;
                    [self performSelectorOnMainThread:@selector(sendConnectionErrorMessage) withObject:nil waitUntilDone:false];
                }
            }
            
            //set the state to receive measure updates
            mode=MEASURE;
            
            //stop the refresh/loading button from spinning
            refreshSpinning = false;
            [self performSelectorOnMainThread:@selector(editTitle:) withObject:@"Home" waitUntilDone:NO];
        }
    // if(self.webview != nil) {
    //     [self.webView stringByEvaluatingJavaScriptFromString:jsStatement];
    // }
}
*/

- (void)execute:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = nil;
    NSString *value = [command.arguments objectAtIndex:0];
    
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)dispose
{
    
}


@end