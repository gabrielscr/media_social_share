#import "MediaSocialSharePlugin.h"
#if __has_include(<media_social_share/media_social_share-Swift.h>)
#import <media_social_share/media_social_share-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "media_social_share-Swift.h"
#endif

@implementation MediaSocialSharePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMediaSocialSharePlugin registerWithRegistrar:registrar];
}
@end
