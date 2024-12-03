//
//  ContentView.swift
//  tfLiteStyleTransferTest
//
//  Created by Yasuo Hasegawa on 2024/12/03.
//

import SwiftUI
import PhotosUI

struct ContentView: View {
    @State var gpuStyleTransferer: StyleTransferer? = nil
    @State var styleImage:UIImage? = nil
    @State var contentImage:UIImage? = nil
    @State var resultImage:UIImage? = nil
    @State var inferenceStatusLabel:String = "---"
    
    @State private var selectedStyleItem: PhotosPickerItem? = nil
    @State private var selectedContentItem: PhotosPickerItem? = nil
    @State private var isPresentingError: Bool = false
    
    @State var isProcessing:Bool = false
    
    var body: some View {
        VStack {
            HStack {
                Text("Status:\(inferenceStatusLabel)")
                    .font(.caption)
                Spacer()
            }
            
            HStack {
                if styleImage != nil {
                    Image(uiImage:styleImage!)
                        .resizable()
                        .scaledToFit()
                } else {
                    Text("Style image is not selected")
                        .padding()
                }
                
                if contentImage != nil {
                    Image(uiImage:contentImage!)
                            .resizable()
                            .scaledToFit()
                } else {
                    Text("Content image is not selected")
                        .padding()
                }
            }
            .padding()

            HStack {
                
                PhotosPicker(
                    selection: $selectedStyleItem,
                    matching: .images,
                    photoLibrary: .shared()
                ) {
                    Text("Select a Style")
                        .customBaseButtonStyle()
                }
                .onChange(of: selectedStyleItem) {
                    if let newItem = selectedStyleItem {
                        Task {
                            styleImage = await getSelectedImage(newItem: newItem)
                        }
                    }
                }
                
                PhotosPicker(
                    selection: $selectedContentItem,
                    matching: .images,
                    photoLibrary: .shared()
                ) {
                    Text("Select a Content")
                        .customBaseButtonStyle()
                }
                .onChange(of: selectedContentItem) {
                    if let newItem = selectedContentItem {
                        Task {
                            contentImage = await getSelectedImage(newItem: newItem)
                        }
                    }
                }
            }
            
            HStack {
                Text("Result:")
                    .font(.caption)
                Spacer()
            }
            
            if resultImage != nil {
                Image(uiImage: resultImage!)
                    .resizable()
                    .scaledToFit()
            }
            
            Button("Run Style Transfer"){
                runStyleTransfer()
            }
            .padding()
            .disabled(isProcessing)
        }
        .padding()
        .onAppear() {
            setupStyleTransfer()
        }
        .alert("Error", isPresented: $isPresentingError) {
            Button("OK", role: .cancel) { }
        } message: {
            Text("Unable to load the selected image.")
        }
    }
    
    func setupStyleTransfer(){
        StyleTransferer.newGPUStyleTransferer { result in
          switch result {
          case .success(let transferer):
              self.gpuStyleTransferer = transferer
              print("Success: \(transferer)")
          case .error(let wrappedError):
            print("Failed to initialize: \(wrappedError)")
          }
        }
    }
    
    func runStyleTransfer() {
        isProcessing = true
        
        let transferer = gpuStyleTransferer

        // Make sure that the style transferer is initialized.
        guard let styleTransferer = transferer else {
          inferenceStatusLabel = "ERROR: Interpreter is not ready."
          return
        }

        guard let targetImage = self.contentImage else {
          inferenceStatusLabel = "ERROR: Select a target image."
          return
        }

        let image = contentImage

        // Make sure that the image is ready before running style transfer.
        guard image != nil else {
          //inferenceStatusLabel.text = "ERROR: Image could not be cropped."
          return
        }

        guard let styleImage = styleImage else {
          inferenceStatusLabel = "ERROR: Select a style image."
          return
        }

        // Run style transfer.
        styleTransferer.runStyleTransfer(
            style: styleImage,
            image: image!,
            completion: { result in
                // Show the result on screen
                switch result {
                    case let .success(styleTransferResult):
                        resultImage = styleTransferResult.resultImage

                        // Show result metadata
                        showInferenceTime(styleTransferResult)
                    case let .error(error):
                        self.inferenceStatusLabel = error.localizedDescription
                }
                isProcessing = false
            }
        )
    }
    
    func showInferenceTime(_ result: StyleTransferResult) {
        let timeString = "Preprocessing: \(Int(result.preprocessingTime * 1000))ms.\n"
          + "Style prediction: \(Int(result.stylePredictTime * 1000))ms.\n"
          + "Style transfer: \(Int(result.styleTransferTime * 1000))ms.\n"
          + "Post-processing: \(Int(result.postprocessingTime * 1000))ms.\n"

        inferenceStatusLabel = timeString
    }
    
    func getSelectedImage(newItem:PhotosPickerItem) async -> UIImage? {
        guard let data = try? await newItem.loadTransferable(type: Data.self) else {
            isPresentingError = true
            return nil
        }
        guard let uiImage = UIImage(data: data) else {
            isPresentingError = true
            return nil
        }
        
        return uiImage
    }
}

//#Preview {
//    ContentView()
//}
