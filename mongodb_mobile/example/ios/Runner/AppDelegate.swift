import UIKit
import Flutter
import StitchCore


@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)

    // in `application(_:didFinishLaunchWithOptions)`
        do {
            _ = try Stitch.initializeDefaultAppClient(
                withClientAppID: "mongoMobile"
            )
            print("Successfully initialized default Stitch app client!");
        } catch {
            // note: This initialization will only fail if an incomplete configuration is
            // passed to a client initialization method, or if a client for a particular
            // app ID is initialized multiple times. See the documentation of the "Stitch"
            // class for more details.
            print("Failed to initialize MongoDB Stitch iOS SDK: \(error)")
        }
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
