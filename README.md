原生的MediaPlayer支持哪些音频和视频格式

因不同格式文件会压缩，尝试使用下面配置无效，所以视频会读取 `Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()` 下的文件内容，也可以在 `VideoActivity#initPlayer` 修改
```groovy
    aaptOptions {
        noCompress ''
    }
```