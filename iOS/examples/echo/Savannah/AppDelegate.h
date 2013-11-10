//
//  AppDelegate.h
//  SavannahEchoExample
//
//  Created by James Ward on 07/10/2013.
//
//

#import <UIKit/UIKit.h>
#import "SVNHWebViewManager.h"
#import "SVNHEchoPlugin.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate, UIWebViewDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

@property (strong, nonatomic) SVNHWebViewManager *webViewManager;

- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;

@end
