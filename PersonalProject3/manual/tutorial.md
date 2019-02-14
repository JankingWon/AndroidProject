# 个人项目3
# 数据存储（一）应用开发

## 第九周任务
## 数据存储（一）

### 基础知识

### 1. SharedPreferences的使用  
#### 1.1 SharedPreferences的读取 
在Android中，用于获取SharedPreferences的接口是getSharedPreferences(String, int)函数。两个参数的意义：   

  * String： Desired preferences file.If a preferences file by this name does not exist,it will be created when you retrieve an editor.   
  * int:  Operating mode.Use 0 or MODE_PRIVATE for the default operation.   

我们对SharedPreferences的读取操作是通过getSharedPreferences(String, int)函数返回的SharedPreferences 对象的⽅法来完成的。查阅[文档](https://developer.android.com/reference/android/content/SharedPreferences.html)可以看到，SharedPreferences ⽀持如下⼏种⽅法读取之前存储的数据：     

  * abstract Map<String, ?> getAll()  
  * abstract boolean getBoolean(String key, boolean defValue)  
  * abstract float getFloat(String key, float defValue)   
  * abstract int getInt(String key, int defValue)  
  * abstract long getLong(String key, long defValue)  
  * abstract String getString(String key, String defValue)  
  * abstract Set<String> getStringSet(String key, Set<String> defValues)  
所有⽅法都需要传⼊⼀个 defValue 参数，在给定的 key 不存在时，SharedPreferences 会直接返回这个默认值。

#### 1.2 SharedPreferences的写入
所有对 SharedPreferences 的写⼊操作，都必须通过 SharedPreferences.edit() 函数返回的 Editor对象来完成。举例：
```java
Context context = getActivity();
SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
// Alternatively, if you need just one shared preference file for your activity, you can use the getPreferences() method:
// SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
SharedPreferences.Editor editor = sharedPref.edit();
editor.putInt("KEY_SCORE", newHighScore);
editor.commit();
```
### 2. Android中的文件操作
Android 中的存储空间分为两种：Internal Storage 和 External Storage.
#### 2.1 Internal Storage
  * 默认情况下，保存在 Internal Storage 的⽂件只有应⽤程序可⻅，其他应⽤，以及⽤⼾本⾝是⽆法访问这些⽂件的。向 Internal Storage 写⼊⽂件的⽰例代码如下：  
```java
  try (FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)) { 
      String str = "Hello, World!";
      fileOutputStream.write(str.getBytes());
      Log.i("TAG", "Successfully saved file.");
  } catch (IOException ex) {
      Log.e("TAG", "Fail to save file.");
  }
```
  若对应的⽂件不存在，openFileOutput(String, int) 函数会直接新建⽂件。注意传⼊的⽂件名参数不能含有 path separators（即 '/'）. 该函数返回⼀个 FileOutputStream 对象，可以调⽤write() ⽅法写⼊内容。
  * 相应地，⽂件的读取可以使⽤ openFileInput(String) 来读取⽂件。该函数返回⼀个 FileInputStream，调⽤read() ⽅法读取内容。  
```java
  try (FileInputStream fileInputStream = openFileInput(FILE_NAME)) {
      byte[] contents = new byte[fileInputStream.available()];
      fileInputStream.read(contents);
  } catch (IOException ex) {
      Log.e("TAG", "Fail to read file.");
  }
```
#### 2.2 External Storage
Android ⽀持使⽤ Java 的⽂件 API 来读写⽂件，但是关键的点在于要有⼀个合适的路径。如果你要存储⼀些公开的，体积较⼤的⽂件（如媒体⽂件），External Storage 就是⼀个⽐较合适的地⽅。如⽂档中所说：

>All Android devices have two file storage areas: “internal” and “external”storage. These names come from the early days of Android, when most devicesoffered built-in non-volatile memory (internal storage), plus a removable storagemedium such as a micro SD card (external storage). Some devices dividethe permanent storage space into “internal” and “external” partitions, so evenwithout a removable storage medium, there are always two storage spaces and theAPI behavior is the same whether the external storage is removable or not.  

⽆论是否⽀持外置 SD 卡，所有的 Android 设备都会将存储空间分为 internal 和 external 两部分。

* 要往 External Storage 写⼊⽂件，需要在 AndroidManifest.xml ⽂件中声明权限：
```xml
<manifest ...>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    ...
</manifest>
```
* 随后调⽤ getExternalFilesDir(String type) 或 Environment.getExternalStoragePublicDirectory()
来获取 SD 卡路径。两者的区别在于：前者指向的⽬录会在应⽤卸载时被删除，⽽后者不会。
* 上⾯的两个函数均返回⼀个 File 对象，代表⼀个⽬录路径，使⽤这个 File 对象，再结合⽂件名，即
可创建 FileInputStream 或 FileOutputStream 来进⾏⽂件读写。
* 举例：
```java
void createExternalStoragePrivateFile() {
    // Create a path where we will place our private file on external
    // storage.
    File file = new File(getExternalFilesDir(null), "DemoFile.jpg");

    try {
        // Very simple code to copy a picture from the application's
        // resource into the external file. Note that this code does
        // no error checking, and assumes the picture is small (does not
        // try to copy it in chunks). Note that if external storage is
        // not currently mounted this will silently fail.
        InputStream is = getResources().openRawResource(R.drawable.balloons);
        OutputStream os = new FileOutputStream(file);
        byte[] data = new byte[is.available()];
        is.read(data);
        os.write(data);
        is.close();
        os.close();
    } catch (IOException e) {
      // Unable to create file, likely because external storage is
      // not currently mounted.
      Log.w("ExternalStorage", "Error writing " + file, e);
  }
}
```

### 参考实现

1. 如何使⽂件编辑 Activity 的 EditText 占据上⽅全部空间？  
使⽤ LinearLayout 和 layout_weight 属性。   
>if there are three text fields and two of them declare a weight of 1, while the other is given no weight, the third text field without weight will not grow and will only occupy the area required by its content. The other two will expand equally to fill the space remaining after all three fields are measured.
更多请查阅[⽂档](https://developer.android.com/guide/topics/ui/layout/linear.html)。
2. 当 Activity 不可⻅时，如何将其从 activity stack 中除去（按返回键直接返回Home）？  
AndroidManifest.xml 中设置 noHistory 属性。  
>Whether or not the activity should be removed from the activity stack and
finished (its finish() method called) when the user navigates away from it and
it’s no longer visible on screen —“true” if it should be finished, and “false”
if not. The default value is “false”.

3. 如何根据需要隐藏/显⽰特定的控件？  
见[文档](https://developer.android.com/guide/topics/ui/layout/linear.html)：
>Set visibility: You can hide or show views using setVisibility(int).
4. 参考⼯程⽬录结构  
 ![preview](https://gitee.com/code_sysu/PersonalProject3/raw/master/manual/images/catalog.png)
