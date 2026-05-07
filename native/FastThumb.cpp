#include <jni.h>
#include <windows.h>
#include <shobjidl.h>
#include <shlwapi.h>
#include <thumbcache.h>
#include <vector>
#include <string>

#pragma comment(lib, "shlwapi.lib")

extern "C" {

JNIEXPORT jintArray JNICALL Java_fastthumb_FastThumb_extractNative(JNIEnv* env, jclass clazz, jstring jpath, jint jsize, jboolean jiconOnly) {
    CoInitialize(NULL);

    const char* path = env->GetStringUTFChars(jpath, NULL);
    if (!path) return NULL;

    int wlen = MultiByteToWideChar(CP_UTF8, 0, path, -1, NULL, 0);
    std::vector<WCHAR> wpath(wlen);
    MultiByteToWideChar(CP_UTF8, 0, path, -1, wpath.data(), wlen);
    env->ReleaseStringUTFChars(jpath, path);

    jintArray result = NULL;
    IShellItemImageFactory* pFactory = NULL;
    
    HRESULT hr = SHCreateItemFromParsingName(wpath.data(), NULL, IID_PPV_ARGS(&pFactory));
    if (SUCCEEDED(hr)) {
        SIZE size = { (LONG)jsize, (LONG)jsize };
        HBITMAP hBitmap = NULL;
        
        // Use default flags to be as compatible as possible
        hr = pFactory->GetImage(size, SIIGBF_BIGGERSIZEOK, &hBitmap);
        if (SUCCEEDED(hr)) {
            BITMAP bmp;
            GetObject(hBitmap, sizeof(BITMAP), &bmp);
            
            int pixelCount = bmp.bmWidth * bmp.bmHeight;
            std::vector<int> pixels(pixelCount);
            
            BITMAPINFO bmi = {0};
            bmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
            bmi.bmiHeader.biWidth = bmp.bmWidth;
            bmi.bmiHeader.biHeight = -bmp.bmHeight;
            bmi.bmiHeader.biPlanes = 1;
            bmi.bmiHeader.biBitCount = 32;
            bmi.bmiHeader.biCompression = BI_RGB;
            
            HDC hdc = GetDC(NULL);
            GetDIBits(hdc, hBitmap, 0, bmp.bmHeight, pixels.data(), &bmi, DIB_RGB_COLORS);
            ReleaseDC(NULL, hdc);
            
            result = env->NewIntArray(pixelCount);
            env->SetIntArrayRegion(result, 0, pixelCount, (const jint*)pixels.data());
            
            DeleteObject(hBitmap);
        }
        pFactory->Release();
    }

    CoUninitialize();
    return result;
}

} // extern "C"
