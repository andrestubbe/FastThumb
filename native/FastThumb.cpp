#include <jni.h>
#include <windows.h>
#include <shobjidl.h>
#include <shlwapi.h>
#include <thumbcache.h>
#include <vector>

#pragma comment(lib, "shlwapi.lib")
#pragma comment(lib, "windowscodecs.lib")

extern "C" {

JNIEXPORT jintArray JNICALL Java_fastthumb_FastThumb_extractNative(JNIEnv* env, jclass clazz, jstring jpath, jint jsize) {
    // 1. Initialize COM
    HRESULT hr = CoInitializeEx(NULL, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
    bool comInitialized = SUCCEEDED(hr);

    const char* path = env->GetStringUTFChars(jpath, NULL);
    if (!path) return NULL;

    // Convert path to WCHAR
    int wlen = MultiByteToWideChar(CP_UTF8, 0, path, -1, NULL, 0);
    std::vector<WCHAR> wpath(wlen);
    MultiByteToWideChar(CP_UTF8, 0, path, -1, wpath.data(), wlen);
    env->ReleaseStringUTFChars(jpath, path);

    jintArray result = NULL;
    IShellItemImageFactory* pFactory = NULL;
    
    // 2. Create Shell Item Image Factory
    hr = SHCreateItemFromParsingName(wpath.data(), NULL, IID_PPV_ARGS(&pFactory));
    if (SUCCEEDED(hr)) {
        SIZE size = { (LONG)jsize, (LONG)jsize };
        HBITMAP hBitmap = NULL;
        
        // 3. Get Image (SIIGBF_BIGGERSIZEOK | SIIGBF_THUMBNAILONLY)
        hr = pFactory->GetImage(size, SIIGBF_BIGGERSIZEOK, &hBitmap);
        if (SUCCEEDED(hr)) {
            // 4. Convert HBITMAP to ARGB
            BITMAP bmp;
            GetObject(hBitmap, sizeof(BITMAP), &bmp);
            
            int pixelCount = bmp.bmWidth * bmp.bmHeight;
            std::vector<int> pixels(pixelCount);
            
            BITMAPINFO bmi = {0};
            bmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
            bmi.bmiHeader.biWidth = bmp.bmWidth;
            bmi.bmiHeader.biHeight = -bmp.bmHeight; // Top-down
            bmi.bmiHeader.biPlanes = 1;
            bmi.bmiHeader.biBitCount = 32;
            bmi.bmiHeader.biCompression = BI_RGB;
            
            HDC hdc = GetDC(NULL);
            GetDIBits(hdc, hBitmap, 0, bmp.bmHeight, pixels.data(), &bmi, DIB_RGB_COLORS);
            ReleaseDC(NULL, hdc);
            
            // Create Java array
            result = env->NewIntArray(pixelCount);
            env->SetIntArrayRegion(result, 0, pixelCount, (const jint*)pixels.data());
            
            DeleteObject(hBitmap);
        }
        pFactory->Release();
    }

    if (comInitialized) CoUninitialize();
    return result;
}

} // extern "C"
