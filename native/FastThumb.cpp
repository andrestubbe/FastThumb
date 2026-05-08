#include <jni.h>
#include <windows.h>
#include <shobjidl.h>
#include <shlwapi.h>
#include <thumbcache.h>
#include <vector>
#include <string>
#include <stdio.h>

#pragma comment(lib, "shlwapi.lib")

struct FastImage {
    int width;
    int height;
    int* pixels;
    bool owned;
};

inline int* alignedAlloc(int count) {
    return (int*)_aligned_malloc(count * sizeof(int), 16);
}

extern "C" {

JNIEXPORT jlong JNICALL Java_fastthumb_FastThumb_getNative(JNIEnv* env, jclass clazz, jstring jpath, jint jsize, jboolean jiconOnly) {
    HRESULT hr = CoInitializeEx(NULL, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
    
    const char* path = env->GetStringUTFChars(jpath, NULL);
    if (!path) return 0;

    int wlen = MultiByteToWideChar(CP_UTF8, 0, path, -1, NULL, 0);
    std::vector<WCHAR> wpath(wlen);
    MultiByteToWideChar(CP_UTF8, 0, path, -1, wpath.data(), wlen);
    env->ReleaseStringUTFChars(jpath, path);

    IShellItemImageFactory* pFactory = NULL;
    jlong resultHandle = 0;
    
    hr = SHCreateItemFromParsingName(wpath.data(), NULL, IID_PPV_ARGS(&pFactory));
    if (FAILED(hr)) {
        printf("[FastThumb] Native: SHCreateItemFromParsingName failed (0x%08X) for path\n", hr);
        return 0;
    }

    SIZE size = { (LONG)jsize, (LONG)jsize };
    HBITMAP hBitmap = NULL;
    
    int flags = SIIGBF_BIGGERSIZEOK | SIIGBF_SCALEUP;
    if (jiconOnly) flags |= SIIGBF_ICONONLY;

    hr = pFactory->GetImage(size, (SIIGBF)flags, &hBitmap);
    
    if (FAILED(hr) && !jiconOnly) {
        printf("[FastThumb] Native: GetImage failed (0x%08X), trying icon fallback...\n", hr);
        hr = pFactory->GetImage(size, (SIIGBF)(SIIGBF_BIGGERSIZEOK | SIIGBF_ICONONLY), &hBitmap);
    }

    if (SUCCEEDED(hr) && hBitmap) {
        BITMAP bmp;
        GetObject(hBitmap, sizeof(BITMAP), &bmp);
        
        int w = bmp.bmWidth;
        int h = bmp.bmHeight;
        
        FastImage* img = new FastImage();
        img->width = w;
        img->height = h;
        img->pixels = alignedAlloc(w * h);
        img->owned = true;
        
        BITMAPINFO bmi = {0};
        bmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
        bmi.bmiHeader.biWidth = w;
        bmi.bmiHeader.biHeight = -h; 
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = BI_RGB;
        
        HDC hdc = GetDC(NULL);
        GetDIBits(hdc, hBitmap, 0, h, img->pixels, &bmi, DIB_RGB_COLORS);
        ReleaseDC(NULL, hdc);
        
        for (int i = 0; i < w * h; i++) {
            unsigned int pixel = (unsigned int)img->pixels[i];
            if ((pixel & 0xFF000000) == 0) {
                img->pixels[i] = (int)(pixel | 0xFF000000);
            }
        }
        
        resultHandle = (jlong)img;
        DeleteObject(hBitmap);
    } else {
        printf("[FastThumb] Native: GetImage failed completely (0x%08X)\n", hr);
    }
    
    pFactory->Release();
    return resultHandle;
}

} // extern "C"
