/********* SecureKeyStore.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "KeychainItemWrapper.h";

@interface SecureKeyStore : CDVPlugin {
  // Member variables go here.
}

- (void)encrypt:(CDVInvokedUrlCommand*)command;
- (void)decrypt:(CDVInvokedUrlCommand*)command;
@end

@implementation SecureKeyStore

- (void)encrypt:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* alias = [command.arguments objectAtIndex:0];
    NSString* input = [command.arguments objectAtIndex:1];

    if (alias != nil && [alias length] > 0) {
        
        KeychainItemWrapper *keychainItem = [[KeychainItemWrapper alloc] initWithIdentifier:@"SKSKeyStore" accessGroup:nil];
        [keychainItem setObject:@input forKey:alias];  
             
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:alias];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)decrypt:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* alias = [command.arguments objectAtIndex:0];

    if (alias != nil && [alias length] > 0) {
        
        NSString *finalText = [keychainItem objectForKey:alias];                          
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:finalText];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
