#import "MongoMobilePlugin.h"
#import <mongo_mobile/mongo_mobile-Swift.h>

@implementation MongoMobilePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMongoMobilePlugin registerWithRegistrar:registrar];
}
@end
