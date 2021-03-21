#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint media_social_share.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'media_social_share'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter Plugin project.'
  s.description      = <<-DESC
A new Flutter Plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.dependency 'FBSDKCoreKit', '~> 8.2.0'
  s.dependency 'FBSDKLoginKit', '~> 8.2.0'
  s.dependency 'FBSDKShareKit', '~> 8.2.0'
  s.platform = :ios, '11'

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'BUILD_LIBRARIES_FOR_DISTRIBUTION' => 'YES', 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
  s.swift_version = '5.3'
end
