# DroiBaaSDemo-Android
本项目展示的是 DroiBaaS 相关功能

## 业务需求
### 一、登录/注册
1. 提供注册/登录功能
2. 修改密码
3. 上传头像/显示头像

### 二、首页banner展示
在首页展示一些banner信息用于活动推广等作用

### 三、app信息list展示
在不同的界面展示不同的app list

### 四、搜索app功能
用关键字对应用进行简单的筛选

## 方案选择

### 传统方案
传统方案需要服务端与客户端开发人员进行沟通，确定交互数据的协议格式。  
服务端负责：搭建业务服务器，进行服务端逻辑代码的编写，提供数据api接口给客户端调用。  
客户端负责：编写界面，使用api接口从服务端读取数据进行数据交互。  

### DroiBaaS方案

#### 一、登录/注册
1. 注册用户

    ``` java
    DroiUser user = new DroiUser();
    user.setUserId(email);
    user.setPassword(password);
    user.signUpInBackground(new DroiCallback<Boolean>() {
        @Override
        public void result(Boolean aBoolean, DroiError droiError) {
            showProgress(false);
            if (droiError.isOk()) {
                // 注册成功
            } else {
                // 注册失败，可以通过 droiError.getCode 获取错误码
            }
        }
    });
    ```

2. 登录

    ``` java
    DroiUser.loginInBackground(email, password, MyUser.class, new DroiCallback<DroiUser>() {
        @Override
        public void result(DroiUser droiUser, DroiError droiError) {
            showProgress(false);
            if (droiError.isOk()) {
                // 登录成功
            } else {
                // 登录失败，可以通过 droiError.getCode 获取错误码
            }
        }
    });
    ```

3. 修改密码

    ``` java
    String oldPassword = "oldPassword";
    String newPassword = "newPassword";
    // 一般从EditText获得
    DroiUser myUser = DroiUser.getCurrentUser();
    if (myUser != null && myUser.isAuthorized() && !myUser.isAnonymous()) {
        myUser.changePasswordInBackground(oldPassword, newPassword, new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                if (aBoolean) {
                    // 修改成功
                } else {
                    // 修改失败
                }
            }
        });
    }
    ```

4. 上传头像

    ``` java
    DroiFile headIcon = new DroiFile(new File(path)); //path 是图片路径
    MyUser user = DroiUser.getCurrentUser(MyUser.class);
    user.setHeadIcon(headIcon);
    user.saveInBackground(new DroiCallback<Boolean>() {
        @Override
        public void result(Boolean aBoolean, DroiError droiError) {
            if (aBoolean) {
                // 上传成功
            } else {
                // 上传失败
            }
        }
    });
    ```

5. 显示头像

    ``` java
    MyUser user = DroiUser.getCurrentUser(MyUser.class);
    if (user != null && user.isAuthorized() && !user.isAnonymous()) {
        if (user.getHeadIcon() != null) {
            user.getHeadIcon().getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    if (droiError.isOk()){
                        // 加载头像
                    }
                }
            });
        }
    }
    ```

#### 二、首页banner展示

``` java
DroiQuery query = DroiQuery.Builder.newBuilder().limit(4).query(Banner.class).build();
query.runQueryInBackground(new DroiQueryCallback<Banner>() {
    @Override
    public void result(List<Banner> list, DroiError droiError) {
        if (droiError.isOk() && list.size() >0) {
            // 通知 view 更新
        } else {
            // 做请求失败处理
        }
    }
});
```

#### 三、app信息list展示
``` java
DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(AppInfo.class).build();
/* 可以给查询设置不同的查询条件，查询不同类别的app等。
DroiCondition cond = DroiCondition.cond("mainType", DroiCondition.Type.EQ, "app");
DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(AppInfo.class).where(cond).build();
*/
query.runQueryInBackground(new DroiQueryCallback<AppInfo>() {
    @Override
    public void result(List<AppInfo> list, DroiError droiError) {
        refreshing = false;
        if (droiError.isOk() && list.size() >0) {
            // 通知 view 更新
        } else {
            // 做请求失败处理
        }
    }
});
```

#### 四、搜索app功能
``` java
// 通过Condition对字段的字符串进行一些筛选
DroiCondition cond = DroiCondition.cond("name", DroiCondition.Type.CONTAINS, appName);
DroiCondition cond1 = DroiCondition.cond("brief", DroiCondition.Type.CONTAINS, appName);
DroiQuery = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(AppInfo.class).where(cond.or(cond1)).build();
query.runQueryInBackground(new DroiQueryCallback<AppInfo>() {
    @Override
    public void result(List<AppInfo> list, DroiError droiError) {
        refreshing = false;
        if (droiError.isOk() && list.size() >0) {
            // 通知 view 更新
        } else {
            // 做请求失败处理
        }
    }
});
```