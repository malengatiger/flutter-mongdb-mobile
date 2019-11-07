#import "MobmongoPlugin.h"
#import <mobmongo/mobmongo-Swift.h>

@implementation MobmongoPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMobmongoPlugin registerWithRegistrar:registrar];
}
@end
