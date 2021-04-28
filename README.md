# AndroidFaceDetection
**以下提供几种解决方案的对比,具体实现请看代码**



- OpenCV (API level 8 +)
  - 识别效果一般,侧脸无法识别.
  - 对识别的距离有限制(2~3米).
  - 如果需要做静态图片识别的话,需要对 Java library层进行修改.
  - 项目里有我编好的动态链接库,拿来就能用,不需要再装官方 OpencvManger.apk 了.
  - 文档:http://www.opencv.org/platforms/android/
- Camera内部的 API (API level 14+)
  - 效果很好.
  - 几乎所有的手机都支持(小米系统相机的人脸检测就是用这种方法做的).
  - 可以识别侧脸.
  - 如果需要做静态图片识别的话,成本很高.
  - 文档:https://developer.android.google.com/reference/android/hardware/Camera.html
- android.media.FaceDetector 静态检测 (API level 1 +)
  - 底层代码:android/external/neven/
  - 只能接受Bitmap 格式的数据.
  - Bitmap 编码格式必须为Bitmap.Config.RGB_565.
  - Bitmap 的宽度一定要是整数.
  - 只能识别双眼距离大于20 像素的人脸像,这个限制应该可以在 FrameWork 中做修改.
  
  - 文档:https://developer.android.google.com/reference/android/media/FaceDetector.html
- Google Play Service 的 Vision API (API 9,在 API 17 增加了一些功能)
  - 非常强大,效果基本能和 Camera API 持平.
  - 静态识别支持比较低清晰度的图片.
  - 可以识别是否睁眼.
  - 可以得到眼睛,鼻子嘴巴等的位置.
  - 有关于情绪的返回值.
  - 可以识别头部姿势.
  - 手机必须安装了 Google 服务框架才能使用.
  - 文档:https://developers.google.com/vision/face-detection-concepts

- Google MLKit face detection
  - https://developers.google.cn/ml-kit/vision/face-detection/android
- Face++ Android SDK
  - 试用需要发邮件申请.
  - 文档很挫.
  - 没有示例 demo.
  - 效果应该没问题.
  - 官网:https://www.faceplusplus.com.cn/
  
 - Dlib
  - http://dlib.net/


