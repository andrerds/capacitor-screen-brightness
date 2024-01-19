import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ScreenBrightnessPlugin)
public class ScreenBrightnessPlugin: CAPPlugin {
    struct BrightnessState {
        let original: CGFloat
        let new: CGFloat
    }
    var state: BrightnessState? = .none

    override public func load() {
        NotificationCenter.default.addObserver(forName: UIApplication.willResignActiveNotification, object: nil, queue: .main) { [weak self] _ in
            guard let self = self else { return }

            if let state = self.state {
                UIScreen.main.brightness = state.original
            }
        }
        NotificationCenter.default.addObserver(forName: UIApplication.didBecomeActiveNotification, object: nil, queue: .main) { [weak self] _ in
            guard let self = self else { return }

            if let state = self.state {
                self.state = BrightnessState(original: UIScreen.main.brightness, new: state.new)
                UIScreen.main.brightness = state.new
            }
        }
    }

    @objc func setBrightness(_ call: CAPPluginCall) {
        let brightness = call.getFloat("brightness", Float(UIScreen.main.brightness))
        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                call.resolve()
                return
            }

            if brightness < 0 {
                if let state = state {
                    UIScreen.main.brightness = state.original
                    self.state = .none
                }
            } else {
                let original = state?.original ?? UIScreen.main.brightness
                state = BrightnessState(original: original, new: CGFloat(brightness))
                UIScreen.main.brightness = CGFloat(brightness)
            }
            call.resolve()
        }
    }

    @objc func getBrightness(_ call: CAPPluginCall) {
        call.resolve([
            "brightness": UIScreen.main.brightness
        ])
    }
}
