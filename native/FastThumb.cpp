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

jlong getNativeInternal(JNIEnv* env, jclass clazz, jstring jpath, jint jsize, jboolean jiconOnly) {
    CoInitializeEx(NULL, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
    const char* path = env->GetStringUTFChars(jpath, NULL);
    if (!path) return 0;

    int wlen = MultiByteToWideChar(CP_UTF8, 0, path, -1, NULL, 0);
    std::vector<WCHAR> wpath(wlen);
    MultiByteToWideChar(CP_UTF8, 0, path, -1, wpath.data(), wlen);
    env->ReleaseStringUTFChars(jpath, path);

    IShellItemImageFactory* pFactory = NULL;
    jlong resultHandle = 0;
    HRESULT hr = SHCreateItemFromParsingName(wpath.data(), NULL, IID_PPV_ARGS(&pFactory));
    if (SUCCEEDED(hr)) {
        SIZE size = { (LONG)jsize, (LONG)jsize };
        HBITMAP hBitmap = NULL;
        int flags = SIIGBF_BIGGERSIZEOK | SIIGBF_SCALEUP;
        if (jiconOnly) flags |= SIIGBF_ICONONLY;
        hr = pFactory->GetImage(size, (SIIGBF)flags, &hBitmap);
        if (SUCCEEDED(hr) && hBitmap) {
            BITMAP bmp;
            GetObject(hBitmap, sizeof(BITMAP), &bmp);
            FastImage* img = new FastImage();
            img->width = bmp.bmWidth;
            img->height = bmp.bmHeight;
            img->pixels = alignedAlloc(bmp.bmWidth * bmp.bmHeight);
            img->owned = true;
            BITMAPV5HEADER bi = {0};
            bi.bV5Size = sizeof(BITMAPV5HEADER);
            bi.bV5Width = bmp.bmWidth; bi.bV5Height = -bmp.bmHeight;
            bi.bV5Planes = 1; bi.bV5BitCount = 32; bi.bV5Compression = BI_BITFIELDS;
            bi.bV5RedMask = 0x00FF0000; bi.bV5GreenMask = 0x0000FF00; bi.bV5BlueMask = 0x000000FF; bi.bV5AlphaMask = 0xFF000000;
            HDC hdc = GetDC(NULL);
            GetDIBits(hdc, hBitmap, 0, bmp.bmHeight, img->pixels, (BITMAPINFO*)&bi, DIB_RGB_COLORS);
            ReleaseDC(NULL, hdc);
            bool hasAlpha = false;
            for (int i = 0; i < bmp.bmWidth * bmp.bmHeight; i++) if ((img->pixels[i] & 0xFF000000) != 0) { hasAlpha = true; break; }
            if (!hasAlpha) for (int i = 0; i < bmp.bmWidth * bmp.bmHeight; i++) img->pixels[i] |= 0xFF000000;
            resultHandle = (jlong)img;
            DeleteObject(hBitmap);
        }
        pFactory->Release();
    }
    return resultHandle;
}

static JNINativeMethod methods[] = {
    {(char*)"getNative", (char*)"(Ljava/lang/String;IZ)J", (void*)getNativeInternal}
};

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_8) != JNI_OK) return JNI_ERR;
    jclass cls = env->FindClass("fastthumb/FastThumb");
    if (cls == NULL) return JNI_ERR;
    if (env->RegisterNatives(cls, methods, 1) < 0) return JNI_ERR;
    return JNI_VERSION_1_8;
}
