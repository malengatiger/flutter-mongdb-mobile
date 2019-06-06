#import "MongodbMobilePlugin.h"
#import <mongodb_mobile/mongodb_mobile-Swift.h>

@implementation MongodbMobilePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMongodbMobilePlugin registerWithRegistrar:registrar];
}
@end
