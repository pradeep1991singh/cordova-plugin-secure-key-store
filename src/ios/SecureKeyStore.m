/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

#import "SecureKeyStore.h"
#import <Cordova/CDV.h>

@implementation SecureKeyStore

- (void) set:(CDVInvokedUrlCommand*)command 
{
  CDVPluginResult* pluginResult = nil;
  NSString* key = [command.arguments objectAtIndex:0];
  NSString* value = [command.arguments objectAtIndex:1];  
  @try {
    KeychainWrapper* keychain = [[KeychainWrapper alloc]init];
    [keychain mySetObject:value forKey:(__bridge id)(kSecValueData)];
    [keychain writeToKeychain];

    [[NSUserDefaults standardUserDefaults]setBool:true forKey:key];
    [[NSUserDefaults standardUserDefaults]synchronize];     

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Key saved to keychain successfully"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  }
  @catch(NSException* exception){
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: saving key into keychain"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  }      
}

- (void) get:(CDVInvokedUrlCommand*)command 
{
  CDVPluginResult* pluginResult = nil;
  NSString* key = [command.arguments objectAtIndex:0];
  @try {

    BOOL hasKey = [[NSUserDefaults standardUserDefaults] boolForKey:key];
    if (hasKey) {
      KeychainWrapper* keychain = [[KeychainWrapper alloc]init];
      NSString *value = [keychain myObjectForKey:@"v_Data"];

      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:value];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];      
    } else {
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: key not found in keychain"];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];      
    }

  }
  @catch(NSException* exception){
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: fetching key from keychain"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  } 
}

- (void) remove:(CDVInvokedUrlCommand*)command 
{
	 	CDVPluginResult* pluginResult = nil;
    NSString* key = (NSString*)[command.arguments objectAtIndex:0];
    @try {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:key];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Key removed successfully"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    @catch(NSException *exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: Could not delete key from keychain"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

@end
