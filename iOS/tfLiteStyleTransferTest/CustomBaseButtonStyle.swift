//
//  CustomBaseButtonStyle.swift
//  tfLiteStyleTransferTest
//
//  Created by Yasuo Hasegawa on 2024/12/03.
//

import SwiftUI

struct CustomBaseButtonStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(10)
            .background(Color.black.opacity(0.7))
            .foregroundColor(.white)
            .cornerRadius(10)
            .contentShape(Rectangle())
    }
}

extension View {
    func customBaseButtonStyle() -> some View {
        self.modifier(CustomBaseButtonStyle())
    }
}
