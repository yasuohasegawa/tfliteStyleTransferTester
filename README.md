# tfliteStyleTransferTester  

`tfliteStyleTransferTester` is a sample app designed to test the TensorFlow Lite Style Transfer model. The models can be downloaded from the following URLs:  

- [Magenta Arbitrary Image Stylization (INT8 Prediction)](https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/style_transfer/ios/magenta_arbitrary-image-stylization-v1-256_int8_prediction_1.tflite)  
- [Magenta Arbitrary Image Stylization (INT8 Transfer)](https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/style_transfer/ios/magenta_arbitrary-image-stylization-v1-256_int8_transfer_1.tflite)  
- [Magenta Arbitrary Image Stylization (FP16 Prediction)](https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/style_transfer/ios/magenta_arbitrary-image-stylization-v1-256_fp16_prediction_1.tflite)  
- [Magenta Arbitrary Image Stylization (FP16 Transfer)](https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/style_transfer/ios/magenta_arbitrary-image-stylization-v1-256_fp16_transfer_1.tflite)  

> **Note:** These models are already included in the project, so you may not need to download them separately.  

## Overview  

The app builds upon the TensorFlow Lite Swift/Kotlin examples for utilizing the Style Transfer model. However, those examples do not include implementations for **SwiftUI** or **Jetpack Compose**. This project was created to test the Style Transfer model using these modern UI frameworks.  

## Current Status  

- **iOS Version**: Fully functional and uses **SwiftUI**.  
- **Android Version**: Fully functional and uses **Jetpack Compose**.  

## iOS Issues  

- **SwiftUI Preview Mode**: Currently, SwiftUI preview mode does not work due to an arm64 architecture issue. However, the project runs without issues on physical devices, and you can use your device to test it.  

## Installation Requirements  

Please refer to the [TensorFlow Lite Style Transfer example documentation](https://github.com/tensorflow/examples/tree/master/lite/examples/style_transfer) for installation instructions and prerequisites.  