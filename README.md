#KUSOmeme

## 项目简介

### V1.0.0

* 1.瀑布流展示猥琐的表情们。

* 2.下拉刷新，每次随机展示20张表情图片。

* 3.表情本地保存

* 4.表情分享

### 技术点

* 1.使用Retrofit、OKHttp对http客户端进行包装，详见RetrofitHelper.java。
* [retrofit](https://github.com/square/retrofit)
* [okhttp](https://github.com/square/okhttp)

* 2.使用Rxjava结合Retrofit进行图片展示，详见ShowMemePresenter.java。
* [RxJava](https://github.com/ReactiveX/RxJava)

* 3.使用Glide展示图片，详见MemeAdapter.java。
* [glide](https://github.com/bumptech/glide)

* 4.使用MVP架构进行重构,详见项目中showmeme包。
* [android-architecture](https://github.com/googlesamples/android-architecture)

## 截屏
<a href="meme/screenshot.jpg"><img src="meme/screenshot.jpg" width="60%"/></a>