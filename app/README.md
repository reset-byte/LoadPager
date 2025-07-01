# LoadPager Sample

这是LoadPager库的示例应用，展示了如何使用`BaseRefreshLoadListFragment`来快速构建具有下拉刷新和上拉加载更多功能的列表页面。**本示例使用玩Android开放API展示真实的网络请求和数据展示。**

## 功能特性

- ✅ 下拉刷新和上拉加载更多
- ✅ 骨架屏加载效果
- ✅ 多种布局管理器支持（线性布局、瀑布流布局）
- ✅ 自定义空视图和错误视图
- ✅ 返回顶部功能
- ✅ MVVM架构设计
- ✅ 支持自定义加载更多视图
- ✅ **真实API集成** - 使用玩Android开放API
- ✅ **网络请求处理** - OkHttp + Gson
- ✅ **JSON数据解析** - 复杂数据结构解析

## API数据源

本示例使用 **[玩Android开放API](https://www.wanandroid.com/blog/show/2)** 作为数据源：

- **接口地址**：`https://www.wanandroid.com/article/list/{page}/json`
- **数据类型**：Android技术文章列表
- **分页机制**：页码从0开始，每页20条数据
- **响应格式**：标准JSON格式，包含errorCode、errorMsg、data字段
- **数据字段**：标题、作者、发布时间、章节分类、文章链接等

### API响应结构

```json
{
  "errorCode": 0,
  "errorMsg": "",
  "data": {
    "curPage": 1,
    "datas": [
      {
        "id": 30207,
        "title": "文章标题",
        "author": "作者名",
        "shareUser": "分享者",
        "niceDate": "2天前",
        "link": "文章链接",
        "chapterName": "章节名",
        "superChapterName": "父章节名"
      }
    ],
    "over": false,
    "pageCount": 807,
    "size": 20,
    "total": 16126
  }
}
```

## Sample结构

### 主要文件

- `MainActivity.kt` - 应用主入口，展示功能介绍和Sample入口
- `sample/SampleActivity.kt` - Sample容器Activity
- `sample/SampleListFragment.kt` - 继承`BaseRefreshLoadListFragment`的列表Fragment
- `sample/Article.kt` - 玩Android文章数据模型
- `sample/WanAndroidResponse.kt` - 玩Android API响应数据模型
- `sample/SampleItemViewBinder.kt` - 文章列表项的ViewBinder

### 布局文件

- `activity_main.xml` - 主Activity布局
- `activity_sample.xml` - Sample Activity布局
- `item_sample.xml` - 文章列表项布局

## 如何使用

### 1. 继承BaseRefreshLoadListFragment

```kotlin
class SampleListFragment : BaseRefreshLoadListFragment<WanAndroidResponse>() {
    // 实现抽象方法
}
```

### 2. 配置Fragment行为

```kotlin
override fun obtainGlobalConfig(): FragmentGlobalConfig {
    val config = FragmentGlobalConfig(context)
    config.layoutManagerType = LayoutManagerType.LINEAR
    config.supportPullToRefresh = true
    config.isShowSkeleton = true
    config.pageSize = 20 // 玩Android API默认页面大小为20
    config.firstPageStartFrom = 0 // 玩Android页码从0开始
    config.loadMoreWhenLeftItemCount = 3
    config.backToTopWhenShowItemCount = 5
    config.minCountToShowLoadFinishView = 20
    config.debug = true
    return config
}
```

### 3. 注册ViewBinder

```kotlin
override fun registerViewBinder(adapter: MultiTypeAdapter) {
    adapter.register(Article::class.java, SampleItemViewBinder())
}
```

### 4. 实现真实网络请求

```kotlin
override fun requestData(options: Map<String, Any>): Flow<ApiResponse<WanAndroidResponse>> {
    return flow {
        try {
            val page = options["page"] as? Int ?: 0
            val apiUrl = "https://www.wanandroid.com/article/list/${page}/json"
            
            // 使用OkHttp发起网络请求
            val client = OkHttpClient()
            val request = Request.Builder().url(apiUrl).get().build()
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    // 使用Gson解析JSON
                    val gson = Gson()
                    val wanAndroidResponse = gson.fromJson(responseBody, WanAndroidResponse::class.java)
                    
                    if (wanAndroidResponse.errorCode == 0) {
                        emit(ApiResponse.Success(wanAndroidResponse))
                    } else {
                        emit(ApiResponse.Error(Exception("API错误: ${wanAndroidResponse.errorMsg}")))
                    }
                }
            } else {
                emit(ApiResponse.Error(Exception("网络请求失败: ${response.code}")))
            }
            response.close()
        } catch (e: Exception) {
            emit(ApiResponse.Error(Exception("请求失败: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)
}
```

### 5. 实现数据处理方法

```kotlin
override fun isLoadMoreFinished(response: WanAndroidResponse?): Boolean {
    // 当over为true表示已经到最后一页了
    return response?.data?.over == true
}

override fun isRequestSuccessButDataEmpty(response: WanAndroidResponse?): Boolean {
    return response?.errorCode == 0 && response.data.datas.isEmpty()
}

override fun obtainListForAdapter(
    response: WanAndroidResponse?,
    currentItems: List<Any>?,
    isRefresh: Boolean
): List<Any> {
    val newArticles = response?.data?.datas ?: emptyList()
    return if (isRefresh) {
        newArticles
    } else {
        (currentItems ?: emptyList()) + newArticles
    }
}
```

## 运行Sample

1. 编译并运行应用
2. 在主界面点击"查看玩Android文章列表"按钮
3. 体验真实API数据加载：
   - 下拉刷新获取最新文章
   - 滚动到底部自动加载更多文章
   - 观察骨架屏加载效果
   - 使用返回顶部功能
   - 查看真实的Android技术文章数据

## 数据展示效果

每个文章项展示的信息包括：
- **文章标题**：完整的文章标题
- **作者信息**：优先显示author，如果为空则显示shareUser
- **分类信息**：superChapterName > chapterName 的分类层级
- **发布时间**：用户友好的时间显示格式（如"2天前"）
- **文章ID**：用于调试和数据追踪

## 网络配置

为了支持网络请求，应用添加了以下配置：

### 1. 网络权限（AndroidManifest.xml）
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<application android:usesCleartextTraffic="true">
```

### 2. 依赖库
- **OkHttp**：用于HTTP网络请求
- **Gson**：用于JSON数据解析
- **Kotlin协程**：用于异步网络操作

## 扩展开发

要在自己的项目中使用LoadPager库集成真实API：

1. **添加依赖**：包含LoadPager库依赖
2. **创建数据模型**：根据API响应结构创建对应的数据类
3. **继承Fragment**：继承`BaseRefreshLoadListFragment`
4. **实现网络请求**：在`requestData`方法中实现具体的网络请求逻辑
5. **配置参数**：在`obtainGlobalConfig`中配置分页参数
6. **注册ViewBinder**：创建并注册列表项的ViewBinder
7. **处理数据**：实现数据处理相关的抽象方法

参考本Sample代码可以快速上手开发真实的列表页面，轻松集成各种REST API。 