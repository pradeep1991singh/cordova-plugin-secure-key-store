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

- (void) writeToSecureKeyStorage:(NSMutableDictionary*) dict 
{
  // get keychain
  KeychainItemWrapper * keychain = [[KeychainItemWrapper alloc] initWithIdentifier:@"cordova.plugins.SecureKeyStore" accessGroup:nil];
  NSString *error;
  
  // Serialize dictionary and store in keychain
  NSData *serializedDict = [NSPropertyListSerialization dataFromPropertyList:dict format:NSPropertyListXMLFormat_v1_0 errorDescription:&error];
  [keychain setObject:serializedDict forKey:(__bridge id)(kSecValueData)];
  if (error) {
      NSLog(@"%@", error);
  }
}

- (NSMutableDictionary *) readFromSecureKeyStorage 
{
  NSMutableDictionary *dict = [NSMutableDictionary dictionary];
  // get keychain
  KeychainItemWrapper * keychain = [[KeychainItemWrapper alloc] initWithIdentifier:@"cordova.plugins.SecureKeyStore" accessGroup:nil];
  NSError *error;
  @try 
  {
      NSData *serializedDict = [keychain objectForKey:(__bridge id)(kSecValueData)];
      NSUInteger dictLength = [serializedDict length];
      if (dictLength) {
          // de-serialize dictionary
          dict = [NSPropertyListSerialization propertyListFromData:serializedDict mutabilityOption:NSPropertyListImmutable format:nil errorDescription:&error];
          if (error) {
              NSLog(@"Read process Exception: %@", error);
          }
      }
  }
  @catch (NSException * exception)
  {
      NSLog(@"Read exception: %@", exception);
  }
  return dict;
}

- (void) removeKeyFromSecureKeyStore:(NSString*) key
{
    // get mutable dictionary and remove key from store
    NSMutableDictionary *dict = [self readFromSecureKeyStorage];
    [dict removeObjectForKey:key];
    [self writeToSecureKeyStorage:dict];     
}

- (void) set:(CDVInvokedUrlCommand*)command 
{
  CDVPluginResult* pluginResult = nil;
  NSString* key = [command.arguments objectAtIndex:0];
  NSString* value = [command.arguments objectAtIndex:1]; 

  @try {
    // set flag
    NSString *keyFlag = value;
    [[NSUserDefaults standardUserDefaults] setObject:keyFlag forKey:key];
    [[NSUserDefaults standardUserDefaults] synchronize];

    // get mutable dictionary and store data
    [self.commandDelegate runInBackground:^{
		@synchronized(self) {
            NSMutableDictionary *dict = [self readFromSecureKeyStorage];
            [dict setValue: value forKey: key];
            [self writeToSecureKeyStorage:dict];     

            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Key saved to keychain successfully"];
            [self.commandDelegate 
            sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
  }
  @catch (NSException* exception)
  {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:exception];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  }
}

- (void) get:(CDVInvokedUrlCommand*)command 
{
  CDVPluginResult* pluginResult = nil;
  NSString* key = [command.arguments objectAtIndex:0];

  @try {
    if ([[NSUserDefaults standardUserDefaults] objectForKey:key]) {
      [self.commandDelegate runInBackground:^{
		    @synchronized(self) {      
            // get mutable dictionaly and retrieve store data
            NSMutableDictionary *dict = [self readFromSecureKeyStorage];
            NSString *value = nil;

            if (dict != nil) {
                value =[dict valueForKey:key];
            }

            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:value];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
      }];
    } else {
      [self removeKeyFromSecureKeyStore:key];
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: key not present in keychain"];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];      
    }
  }
  @catch (NSException* exception)
  {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: fetching key from keychain"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  } 
}

- (void) remove:(CDVInvokedUrlCommand*)command 
{
  CDVPluginResult* pluginResult = nil;
  NSString* key = (NSString*)[command.arguments objectAtIndex:0];
  @try {
      // remove key flag
      [[NSUserDefaults standardUserDefaults] removeObjectForKey:key]; 
      [[NSUserDefaults standardUserDefaults] synchronize];

      [self.commandDelegate runInBackground:^{
          @synchronized(self) {      
              [self removeKeyFromSecureKeyStore:key];
              CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Key removed successfully"];
              [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
          }
      }];   
  }
  @catch(NSException *exception) {
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"Exception: Could not delete key from keychain"];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  }
}

@end
