<<<<<<< HEAD
<<<<<<< HEAD
# 实验一
# 中山大学智慧健康服务平台应用开发

---  

---
## 第四周任务
## 基本的UI界面设计
### 基础知识
Android的组件分为布局和控件。  
布局，就是让控件在里面按一定的次序排列好的一种组件，本身并不提供内容。  
控件，就是显示内容的组件，比如显示一张图片，显示文字等等。  
最基本也最常用的布局有以下四种：LinearLayout、RelativeLayout、TableLayout、FrameLayout。  
最常用的控件有以下几种：用于显示文字的 TextView、用于显示图片的ImageView、用于接受用户输入的输入框EditText、按钮Button、单选按钮RadioButton，等等。  

以下简要介绍本次实验使用到的组件。  
#### ConstraintLayout
约束布局，布局内组件按各种约束排列的布局。每个组件受到三类约束，即其他组件，父容器(parent)，基准线(GuideLine)。
约束布局代码可归纳为以下形式：  
  
**app:layout_constraint[组件本身的位置]_to[目标位置]Of="[目标id]"**  
  
例如
```javascript
    <Button
        android:id="@+id/btn0" />
    <Button
        android:id="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@id/btn0" />
```
这一段代码便是将 btn1 按钮的左边界与 btn0 按钮的右边界进行对齐，效果如图  
 ![align](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/align.jpg) 

若想要组件水平居中，只需设置组件的左右边界与父容器的左右边界分别对齐。同理，组件的垂直居中也可以这么设置。  

当确定好一个组件的左右边界范围后，若这个组件的宽度设置为wrap_content，则该组件会在这个范围中居中显示，若宽度设置为0dp，则该组件会自动拉伸宽度，将这个边界范围全部占满。  
例如
```javascript
    <Button
        android:id="@+id/btn0" />
    <Button
        android:layout_width="0dp"
        android:id="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@id/btn0"
        app:layout_constraintRight_toRightOf="parent" />
```
效果图  ![fill](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/fill.jpg) 

#### TextView
用于显示文字内容的控件，通过设置 text 的值来显示要显示的内容，常用的属性有 textColor，用于设置文字的颜色，textSize，用于设置文字大小。  
```javascript
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" 
        android:textSize="20sp"
        android:textColor="@color/colorAccent"
        android:text="实验" />
```
效果图:
 ![test](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/test.jpg) 

关于@color/colorAccent 这种形式的资源引用后面会讲。
#### EditText
用于接受用户输入的输入框，常用属性除了和 TextView 相同的 textColor 和 textSize 之外，还有inputType，用于设置输入框中文本的类型，如果设置为textPassword，会使输入的文字变为小点(·)，hint，用于设置当输入框为空时的提示内容。  
```javascript
    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:hint="这是一个输入框" />
```
效果图:   ![edit](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/edit.jpg)  
#### ImageView
显示图片的控件，通过设置src来设置要显示的图片。  
```javascript
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        android:src="@mipmap/sysu" />
```
 ![sysu](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/sysu.jpg)   
关于ImageView的src和background属性的区别，自行查阅资料。
#### RadioButton和RadioGroup
RadioButton是单选按钮，一组单选按钮需要包括在一个RadioGroup中，并且需要对RadioGroup和其包括的每一个RadioButton都设置 id，RadioButton 才能正常工作。   
可通过设置 checked 属性来使某个RadioButton被选中。  
```javascript
    <RadioGroup
        android:id="@+id/rb0"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <RadioButton
            android:id="@+id/rb1"
            style="@style/MyRadioButton"
            android:text="man" />

        <RadioButton
            android:id="@+id/rb2"
            style="@style/MyRadioButton"
            android:text="woman" />
            
    </RadioGroup>
```

组件的介绍就到这里，下面简单介绍以下几个重要的通用属性
#### layout_width和layout_height
这两个属性分别指定所属组件的宽度和高度，属性值可以是具体的距离，如：20dp，更常见的是指定为match_parent或者wrap_content，match_parent指的是组件的宽度或高度与父组件的宽度或高度一致，如果组件没有父组件，那么组件的宽度或高度与屏幕的宽度或高度一致。wrap_content指组件的宽度或高度刚好可以包裹住组件内的子组件即可。
#### layout_gravity和gravity
这两个属性的基本属性值有五种：top、bottom、center、left、right，可以使用组合属性，如left|bottom表示左下角。  
区别在于layout_gravity用于指定设置该属性的组件相对于父组件的位置，gravity用于指定指定设置该属性的组件下的子组件的位置。
#### layout_margin和padding
layout_margin指定外边距，padding指定内边距，这两个属性配合上四个方向还各有四个属性，如layout_marginTop，paddingTop等。
#### 关于自定义背景边框
当需要将一个button设置为圆角矩形时，光设置button的属性是达不到效果的，需要定义一个背景边框来达到这个效果。这种自定义的背景边框定义在 drawable 文件夹下，所以为了不把它和图片混杂在一起，习惯上把图片放在mipmap文件夹下。  
定义的方法如下：  
在drawable文件夹下新建一个Drawable resource file，填写file name，然后把自动生成的selector标签改为shape，shape下有多个属性，padding，radius，solid等等。圆角矩形的生成主要是修改corner属性，具体自行查阅资料。  

最后，在Button中设置background为@drawable/加上刚刚填写的文件名即可引用这个自定义的背景。

#### 如何在应用中显示布局
首先，需要在res/layout文件夹下写好布局文件，  
 ![tree1](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/tree1.jpg) 

然后创建一个java文件，在该文件中将布局引入，  
 ![tree2](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/tree2.jpg) 

然后在注册文件中注册，  
```javascript
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```
将该Activity设置为应用启动时第一个加载的Activity，
```javascript
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>
```
然后就可以运行了。
### 拓展知识
#### 关于资源的引用
Android项目中不建议使用硬编码来使用资源，建议将各类资源定义在res文件夹中的values文件夹下，字符串资源定义在strings.xml中，颜色资源定义在colors.xml中，距离，字体大小资源定义在dimens.xml中。图片资源例外，是存放在res文件夹中的mipmap文件夹下或者drawable文件夹下。  

*示例colors.xml*
```javascript
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#3F51B5</color>
    <color name="colorPrimaryDark">#303F9F</color>
    <color name="colorAccent">#FF4081</color>
    <color name="colorWhite">#FFFFFF</color>
    <color name="colorBlack">#000000</color>
</resources>
```
通过键值对来定义，使用的时候用@color/colorAccent 即可引用到对应资源，注意是*@color*不是@colors，这里和文件名是不相同的。其它资源的引用也一样。
#### 关于自定义style
style定义在res/values/styles.xml文件中，也是一种资源。例如当多个TextView具有相同的layout_height，layout_width，textSize，textColor设定时，如果每次都要把这些属性设定一次会很烦，而且如果全部需要修改的话（改变字体大小）也不方便，因此可以把这些属性统一定义为一个style，这样使用的时候只要直接引用这个style即可。

*示例styles.xml*
```javascript
    <style name="myEditTextStyle">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:textColor">@color/colorPrimaryDark</item>
        <item name="android:textSize">@dimen/normal_size</item>
    </style>
```
一个自定义 style 下包含多个键值对，引用的时候设置 style=“@style/myEditTextStyle”即可。在引用 style 之后，还可以继续定义属性，如果有重复，以重复定义的属性为准。

---

---

## 第五周任务
## 基础的事件处理
---
### 基础知识 
#### 在java文件中引用布局文件中的控件 
在上一次实验中，在onCreateView(Bundle savedInstanceState)方法中调用setContentView()方法将布局加载进来。如果需要用到布局中的某些控件的话，首先需要给控件一个id
```javascript
    <Button
        android:id="@+id/btn0"
        style="@style/MyButtonStyle"/>
```
定义 id 的语法和引用资源类似，@+id/id名称，在同一个布局文件中不允许有重复的id，即使是不同控件也不行，但是不同的布局文件中可以使用同一个id之后在java文件中将布局加载之后，也就是setContentView()之后，使用findViewById()方法可以获得该控件：
```javascript
    Button btn0 = (Button) findViewById(R.id.btn0);
```
findViewById()方法带一个参数，就是刚刚定义的那个id，参数形式为R.id.XXX，其中XXX 就是刚刚定义的那个 id，由于 findViewById()方法返回的是一个View类型，所以需要强制类型转换为Button类型。获得这个 Button 之后，就可以对这个 Button 进行后续的操作了。

#### 弹出Toast信息
在需要弹出Toast信息的地方，写上：
```javascript
    Toast.makeText(MainActivity.this, "A Toast", Toast.LENGTH_SHORT).show();
```
这个方法带三个参数，context，text，duration。  
context是一个上下文类型，写上使用这个方法的 java 类名加上.this即可;  
text是Toast要显示的信息；
duration是Toast显示的时间长度，有Toast.LENGTH_SHORT和Toast.LENGTH_LONG可选，最后记得调用show()方法将Toast显示出来。

#### 事件处理
此次实验用到的事件处理方法一共两个，一个是Button的单击事件，button.setOnClickListener()，一个是RadioGroup的切换事件，radioGroup.setOnCheckedChangeListener()，下面以Button为例简单介绍一下事件处理。  
要处理事件，首先要将与该事件有关的控件引入进来，比如要判断搜索内容是否为空，那么除了Button需要引入外（因为Button是触发事件的主体），还需要引入搜索内容的输入框。  
引入之后，button.setOnClickListener()中做处理，这里的button是一个变量名，记得换成自己定义的变量名。
```javascript
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //事件处理
        }
    });
```
onClick方法带的view参数是触发这个单击事件的控件的引用，在这个例子中，view就是触发事件的Button，在onClick()方法中做事件的处理，例如判断搜索内容是否为空：
```javascript
Button button = (Button) findViewById(R.id.btn0);
EditText searchContent = (EditText) findViewById(R.id.search);

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(TextUtils.isEmpty(searchContent.getText().toString())) {
            // 弹出Toast消息
        }
    }
});
```
#### 简单对话框的使用
Android 中最基本的对话框是AlertDialog，之所以说它最简单，是因为布局和使用的方法都很简单，布局是写好的。  
标题，通过 setTitle()方法设置；  
图标，通过 setIcon()方法设置；  
显示在中间的主要信息，通过setMessage()方法显示，等等。  

显示一个AlertDialog 的基本步骤如下：  
1. 创建AlertDialog.Builder对象；  
2. 调用上面的方法进行设置；  
3. 如果需要设置取消按钮，方法原型如下：
```javascript
setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击后的事件处理
            }
        })
```
确定按钮怎么设置自行查阅资料。  
4. 调用AlertDialog.Builder的create()方法创建AlertDialog对象，再调用AlertDialog对象的show()方法将对话框显示出来。

### 拓展知识
有兴趣的同学可以查阅Snackbar,TextInputLayout这两个控件的使用方法。

=======
# 实验一
# 中山大学智慧健康服务平台应用开发

---  

---
## 第四周任务
## 基本的UI界面设计
### 基础知识
Android的组件分为布局和控件。  
布局，就是让控件在里面按一定的次序排列好的一种组件，本身并不提供内容。  
控件，就是显示内容的组件，比如显示一张图片，显示文字等等。  
最基本也最常用的布局有以下四种：LinearLayout、RelativeLayout、TableLayout、FrameLayout。  
最常用的控件有以下几种：用于显示文字的 TextView、用于显示图片的ImageView、用于接受用户输入的输入框EditText、按钮Button、单选按钮RadioButton，等等。  

以下简要介绍本次实验使用到的组件。  
#### ConstraintLayout
约束布局，布局内组件按各种约束排列的布局。每个组件受到三类约束，即其他组件，父容器(parent)，基准线(GuideLine)。
约束布局代码可归纳为以下形式：  
  
**app:layout_constraint[组件本身的位置]_to[目标位置]Of="[目标id]"**  
  
例如
```javascript
    <Button
        android:id="@+id/btn0" />
    <Button
        android:id="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@id/btn0" />
```
这一段代码便是将 btn1 按钮的左边界与 btn0 按钮的右边界进行对齐，效果如图  
 ![align](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/align.jpg) 

若想要组件水平居中，只需设置组件的左右边界与父容器的左右边界分别对齐。同理，组件的垂直居中也可以这么设置。  

当确定好一个组件的左右边界范围后，若这个组件的宽度设置为wrap_content，则该组件会在这个范围中居中显示，若宽度设置为0dp，则该组件会自动拉伸宽度，将这个边界范围全部占满。  
例如
```javascript
    <Button
        android:id="@+id/btn0" />
    <Button
        android:layout_width="0dp"
        android:id="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@id/btn0"
        app:layout_constraintRight_toRightOf="parent" />
```
效果图  ![fill](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/fill.jpg) 

#### TextView
用于显示文字内容的控件，通过设置 text 的值来显示要显示的内容，常用的属性有 textColor，用于设置文字的颜色，textSize，用于设置文字大小。  
```javascript
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" 
        android:textSize="20sp"
        android:textColor="@color/colorAccent"
        android:text="实验" />
```
效果图:
 ![test](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/test.jpg) 

关于@color/colorAccent 这种形式的资源引用后面会讲。
#### EditText
用于接受用户输入的输入框，常用属性除了和 TextView 相同的 textColor 和 textSize 之外，还有inputType，用于设置输入框中文本的类型，如果设置为textPassword，会使输入的文字变为小点(·)，hint，用于设置当输入框为空时的提示内容。  
```javascript
    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:hint="这是一个输入框" />
```
效果图:   ![edit](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/edit.jpg)  
#### ImageView
显示图片的控件，通过设置src来设置要显示的图片。  
```javascript
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        android:src="@mipmap/sysu" />
```
 ![sysu](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/sysu.jpg)   
关于ImageView的src和background属性的区别，自行查阅资料。
#### RadioButton和RadioGroup
RadioButton是单选按钮，一组单选按钮需要包括在一个RadioGroup中，并且需要对RadioGroup和其包括的每一个RadioButton都设置 id，RadioButton 才能正常工作。   
可通过设置 checked 属性来使某个RadioButton被选中。  
```javascript
    <RadioGroup
        android:id="@+id/rb0"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <RadioButton
            android:id="@+id/rb1"
            style="@style/MyRadioButton"
            android:text="man" />

        <RadioButton
            android:id="@+id/rb2"
            style="@style/MyRadioButton"
            android:text="woman" />
            
    </RadioGroup>
```

组件的介绍就到这里，下面简单介绍以下几个重要的通用属性
#### layout_width和layout_height
这两个属性分别指定所属组件的宽度和高度，属性值可以是具体的距离，如：20dp，更常见的是指定为match_parent或者wrap_content，match_parent指的是组件的宽度或高度与父组件的宽度或高度一致，如果组件没有父组件，那么组件的宽度或高度与屏幕的宽度或高度一致。wrap_content指组件的宽度或高度刚好可以包裹住组件内的子组件即可。
#### layout_gravity和gravity
这两个属性的基本属性值有五种：top、bottom、center、left、right，可以使用组合属性，如left|bottom表示左下角。  
区别在于layout_gravity用于指定设置该属性的组件相对于父组件的位置，gravity用于指定指定设置该属性的组件下的子组件的位置。
#### layout_margin和padding
layout_margin指定外边距，padding指定内边距，这两个属性配合上四个方向还各有四个属性，如layout_marginTop，paddingTop等。
#### 关于自定义背景边框
当需要将一个button设置为圆角矩形时，光设置button的属性是达不到效果的，需要定义一个背景边框来达到这个效果。这种自定义的背景边框定义在 drawable 文件夹下，所以为了不把它和图片混杂在一起，习惯上把图片放在mipmap文件夹下。  
定义的方法如下：  
在drawable文件夹下新建一个Drawable resource file，填写file name，然后把自动生成的selector标签改为shape，shape下有多个属性，padding，radius，solid等等。圆角矩形的生成主要是修改corner属性，具体自行查阅资料。  

最后，在Button中设置background为@drawable/加上刚刚填写的文件名即可引用这个自定义的背景。

#### 如何在应用中显示布局
首先，需要在res/layout文件夹下写好布局文件，  
 ![tree1](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/tree1.jpg) 

然后创建一个java文件，在该文件中将布局引入，  
 ![tree2](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/tree2.jpg) 

然后在注册文件中注册，  
```javascript
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```
将该Activity设置为应用启动时第一个加载的Activity，
```javascript
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>
```
然后就可以运行了。
### 拓展知识
#### 关于资源的引用
Android项目中不建议使用硬编码来使用资源，建议将各类资源定义在res文件夹中的values文件夹下，字符串资源定义在strings.xml中，颜色资源定义在colors.xml中，距离，字体大小资源定义在dimens.xml中。图片资源例外，是存放在res文件夹中的mipmap文件夹下或者drawable文件夹下。  

*示例colors.xml*
```javascript
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#3F51B5</color>
    <color name="colorPrimaryDark">#303F9F</color>
    <color name="colorAccent">#FF4081</color>
    <color name="colorWhite">#FFFFFF</color>
    <color name="colorBlack">#000000</color>
</resources>
```
通过键值对来定义，使用的时候用@color/colorAccent 即可引用到对应资源，注意是*@color*不是@colors，这里和文件名是不相同的。其它资源的引用也一样。
#### 关于自定义style
style定义在res/values/styles.xml文件中，也是一种资源。例如当多个TextView具有相同的layout_height，layout_width，textSize，textColor设定时，如果每次都要把这些属性设定一次会很烦，而且如果全部需要修改的话（改变字体大小）也不方便，因此可以把这些属性统一定义为一个style，这样使用的时候只要直接引用这个style即可。

*示例styles.xml*
```javascript
    <style name="myEditTextStyle">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:textColor">@color/colorPrimaryDark</item>
        <item name="android:textSize">@dimen/normal_size</item>
    </style>
```
一个自定义 style 下包含多个键值对，引用的时候设置 style=“@style/myEditTextStyle”即可。在引用 style 之后，还可以继续定义属性，如果有重复，以重复定义的属性为准。

---

---

## 第五周任务
## 基础的事件处理
---
### 基础知识 
#### 在java文件中引用布局文件中的控件 
在上一次实验中，在onCreateView(Bundle savedInstanceState)方法中调用setContentView()方法将布局加载进来。如果需要用到布局中的某些控件的话，首先需要给控件一个id
```javascript
    <Button
        android:id="@+id/btn0"
        style="@style/MyButtonStyle"/>
```
定义 id 的语法和引用资源类似，@+id/id名称，在同一个布局文件中不允许有重复的id，即使是不同控件也不行，但是不同的布局文件中可以使用同一个id之后在java文件中将布局加载之后，也就是setContentView()之后，使用findViewById()方法可以获得该控件：
```javascript
    Button btn0 = (Button) findViewById(R.id.btn0);
```
findViewById()方法带一个参数，就是刚刚定义的那个id，参数形式为R.id.XXX，其中XXX 就是刚刚定义的那个 id，由于 findViewById()方法返回的是一个View类型，所以需要强制类型转换为Button类型。获得这个 Button 之后，就可以对这个 Button 进行后续的操作了。

#### 弹出Toast信息
在需要弹出Toast信息的地方，写上：
```javascript
    Toast.makeText(MainActivity.this, "A Toast", Toast.LENGTH_SHORT).show();
```
这个方法带三个参数，context，text，duration。  
context是一个上下文类型，写上使用这个方法的 java 类名加上.this即可;  
text是Toast要显示的信息；
duration是Toast显示的时间长度，有Toast.LENGTH_SHORT和Toast.LENGTH_LONG可选，最后记得调用show()方法将Toast显示出来。

#### 事件处理
此次实验用到的事件处理方法一共两个，一个是Button的单击事件，button.setOnClickListener()，一个是RadioGroup的切换事件，radioGroup.setOnCheckedChangeListener()，下面以Button为例简单介绍一下事件处理。  
要处理事件，首先要将与该事件有关的控件引入进来，比如要判断搜索内容是否为空，那么除了Button需要引入外（因为Button是触发事件的主体），还需要引入搜索内容的输入框。  
引入之后，button.setOnClickListener()中做处理，这里的button是一个变量名，记得换成自己定义的变量名。
```javascript
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //事件处理
        }
    });
```
onClick方法带的view参数是触发这个单击事件的控件的引用，在这个例子中，view就是触发事件的Button，在onClick()方法中做事件的处理，例如判断搜索内容是否为空：
```javascript
Button button = (Button) findViewById(R.id.btn0);
EditText searchContent = (EditText) findViewById(R.id.search);

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(TextUtils.isEmpty(searchContent.getText().toString())) {
            // 弹出Toast消息
        }
    }
});
```
#### 简单对话框的使用
Android 中最基本的对话框是AlertDialog，之所以说它最简单，是因为布局和使用的方法都很简单，布局是写好的。  
标题，通过 setTitle()方法设置；  
图标，通过 setIcon()方法设置；  
显示在中间的主要信息，通过setMessage()方法显示，等等。  

显示一个AlertDialog 的基本步骤如下：  
1. 创建AlertDialog.Builder对象；  
2. 调用上面的方法进行设置；  
3. 如果需要设置取消按钮，方法原型如下：
```javascript
setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击后的事件处理
            }
        })
```
确定按钮怎么设置自行查阅资料。  
4. 调用AlertDialog.Builder的create()方法创建AlertDialog对象，再调用AlertDialog对象的show()方法将对话框显示出来。

### 拓展知识
有兴趣的同学可以查阅Snackbar,TextInputLayout这两个控件的使用方法。

>>>>>>> b1f83086931dc585c84375c3426ba41351a01c34
=======
# 实验一
# 中山大学智慧健康服务平台应用开发

---  

---
## 第四周任务
## 基本的UI界面设计
### 基础知识
Android的组件分为布局和控件。  
布局，就是让控件在里面按一定的次序排列好的一种组件，本身并不提供内容。  
控件，就是显示内容的组件，比如显示一张图片，显示文字等等。  
最基本也最常用的布局有以下四种：LinearLayout、RelativeLayout、TableLayout、FrameLayout。  
最常用的控件有以下几种：用于显示文字的 TextView、用于显示图片的ImageView、用于接受用户输入的输入框EditText、按钮Button、单选按钮RadioButton，等等。  

以下简要介绍本次实验使用到的组件。  
#### ConstraintLayout
约束布局，布局内组件按各种约束排列的布局。每个组件受到三类约束，即其他组件，父容器(parent)，基准线(GuideLine)。
约束布局代码可归纳为以下形式：  
  
**app:layout_constraint[组件本身的位置]_to[目标位置]Of="[目标id]"**  
  
例如
```javascript
    <Button
        android:id="@+id/btn0" />
    <Button
        android:id="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@id/btn0" />
```
这一段代码便是将 btn1 按钮的左边界与 btn0 按钮的右边界进行对齐，效果如图  
 ![align](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/align.jpg) 

若想要组件水平居中，只需设置组件的左右边界与父容器的左右边界分别对齐。同理，组件的垂直居中也可以这么设置。  

当确定好一个组件的左右边界范围后，若这个组件的宽度设置为wrap_content，则该组件会在这个范围中居中显示，若宽度设置为0dp，则该组件会自动拉伸宽度，将这个边界范围全部占满。  
例如
```javascript
    <Button
        android:id="@+id/btn0" />
    <Button
        android:layout_width="0dp"
        android:id="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@id/btn0"
        app:layout_constraintRight_toRightOf="parent" />
```
效果图  ![fill](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/fill.jpg) 

#### TextView
用于显示文字内容的控件，通过设置 text 的值来显示要显示的内容，常用的属性有 textColor，用于设置文字的颜色，textSize，用于设置文字大小。  
```javascript
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" 
        android:textSize="20sp"
        android:textColor="@color/colorAccent"
        android:text="实验" />
```
效果图:
 ![test](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/test.jpg) 

关于@color/colorAccent 这种形式的资源引用后面会讲。
#### EditText
用于接受用户输入的输入框，常用属性除了和 TextView 相同的 textColor 和 textSize 之外，还有inputType，用于设置输入框中文本的类型，如果设置为textPassword，会使输入的文字变为小点(·)，hint，用于设置当输入框为空时的提示内容。  
```javascript
    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:hint="这是一个输入框" />
```
效果图:   ![edit](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/edit.jpg)  
#### ImageView
显示图片的控件，通过设置src来设置要显示的图片。  
```javascript
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        android:src="@mipmap/sysu" />
```
 ![sysu](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/sysu.jpg)   
关于ImageView的src和background属性的区别，自行查阅资料。
#### RadioButton和RadioGroup
RadioButton是单选按钮，一组单选按钮需要包括在一个RadioGroup中，并且需要对RadioGroup和其包括的每一个RadioButton都设置 id，RadioButton 才能正常工作。   
可通过设置 checked 属性来使某个RadioButton被选中。  
```javascript
    <RadioGroup
        android:id="@+id/rb0"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <RadioButton
            android:id="@+id/rb1"
            style="@style/MyRadioButton"
            android:text="man" />

        <RadioButton
            android:id="@+id/rb2"
            style="@style/MyRadioButton"
            android:text="woman" />
            
    </RadioGroup>
```

组件的介绍就到这里，下面简单介绍以下几个重要的通用属性
#### layout_width和layout_height
这两个属性分别指定所属组件的宽度和高度，属性值可以是具体的距离，如：20dp，更常见的是指定为match_parent或者wrap_content，match_parent指的是组件的宽度或高度与父组件的宽度或高度一致，如果组件没有父组件，那么组件的宽度或高度与屏幕的宽度或高度一致。wrap_content指组件的宽度或高度刚好可以包裹住组件内的子组件即可。
#### layout_gravity和gravity
这两个属性的基本属性值有五种：top、bottom、center、left、right，可以使用组合属性，如left|bottom表示左下角。  
区别在于layout_gravity用于指定设置该属性的组件相对于父组件的位置，gravity用于指定指定设置该属性的组件下的子组件的位置。
#### layout_margin和padding
layout_margin指定外边距，padding指定内边距，这两个属性配合上四个方向还各有四个属性，如layout_marginTop，paddingTop等。
#### 关于自定义背景边框
当需要将一个button设置为圆角矩形时，光设置button的属性是达不到效果的，需要定义一个背景边框来达到这个效果。这种自定义的背景边框定义在 drawable 文件夹下，所以为了不把它和图片混杂在一起，习惯上把图片放在mipmap文件夹下。  
定义的方法如下：  
在drawable文件夹下新建一个Drawable resource file，填写file name，然后把自动生成的selector标签改为shape，shape下有多个属性，padding，radius，solid等等。圆角矩形的生成主要是修改corner属性，具体自行查阅资料。  

最后，在Button中设置background为@drawable/加上刚刚填写的文件名即可引用这个自定义的背景。

#### 如何在应用中显示布局
首先，需要在res/layout文件夹下写好布局文件，  
 ![tree1](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/tree1.jpg) 

然后创建一个java文件，在该文件中将布局引入，  
 ![tree2](https://gitee.com/code_sysu/PersonalProject1/raw/master/manual/images/tree2.jpg) 

然后在注册文件中注册，  
```javascript
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```
将该Activity设置为应用启动时第一个加载的Activity，
```javascript
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>
```
然后就可以运行了。
### 拓展知识
#### 关于资源的引用
Android项目中不建议使用硬编码来使用资源，建议将各类资源定义在res文件夹中的values文件夹下，字符串资源定义在strings.xml中，颜色资源定义在colors.xml中，距离，字体大小资源定义在dimens.xml中。图片资源例外，是存放在res文件夹中的mipmap文件夹下或者drawable文件夹下。  

*示例colors.xml*
```javascript
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#3F51B5</color>
    <color name="colorPrimaryDark">#303F9F</color>
    <color name="colorAccent">#FF4081</color>
    <color name="colorWhite">#FFFFFF</color>
    <color name="colorBlack">#000000</color>
</resources>
```
通过键值对来定义，使用的时候用@color/colorAccent 即可引用到对应资源，注意是*@color*不是@colors，这里和文件名是不相同的。其它资源的引用也一样。
#### 关于自定义style
style定义在res/values/styles.xml文件中，也是一种资源。例如当多个TextView具有相同的layout_height，layout_width，textSize，textColor设定时，如果每次都要把这些属性设定一次会很烦，而且如果全部需要修改的话（改变字体大小）也不方便，因此可以把这些属性统一定义为一个style，这样使用的时候只要直接引用这个style即可。

*示例styles.xml*
```javascript
    <style name="myEditTextStyle">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:textColor">@color/colorPrimaryDark</item>
        <item name="android:textSize">@dimen/normal_size</item>
    </style>
```
一个自定义 style 下包含多个键值对，引用的时候设置 style=“@style/myEditTextStyle”即可。在引用 style 之后，还可以继续定义属性，如果有重复，以重复定义的属性为准。

---

---

## 第五周任务
## 基础的事件处理
---
### 基础知识 
#### 在java文件中引用布局文件中的控件 
在上一次实验中，在onCreateView(Bundle savedInstanceState)方法中调用setContentView()方法将布局加载进来。如果需要用到布局中的某些控件的话，首先需要给控件一个id
```javascript
    <Button
        android:id="@+id/btn0"
        style="@style/MyButtonStyle"/>
```
定义 id 的语法和引用资源类似，@+id/id名称，在同一个布局文件中不允许有重复的id，即使是不同控件也不行，但是不同的布局文件中可以使用同一个id之后在java文件中将布局加载之后，也就是setContentView()之后，使用findViewById()方法可以获得该控件：
```javascript
    Button btn0 = (Button) findViewById(R.id.btn0);
```
findViewById()方法带一个参数，就是刚刚定义的那个id，参数形式为R.id.XXX，其中XXX 就是刚刚定义的那个 id，由于 findViewById()方法返回的是一个View类型，所以需要强制类型转换为Button类型。获得这个 Button 之后，就可以对这个 Button 进行后续的操作了。

#### 弹出Toast信息
在需要弹出Toast信息的地方，写上：
```javascript
    Toast.makeText(MainActivity.this, "A Toast", Toast.LENGTH_SHORT).show();
```
这个方法带三个参数，context，text，duration。  
context是一个上下文类型，写上使用这个方法的 java 类名加上.this即可;  
text是Toast要显示的信息；
duration是Toast显示的时间长度，有Toast.LENGTH_SHORT和Toast.LENGTH_LONG可选，最后记得调用show()方法将Toast显示出来。

#### 事件处理
此次实验用到的事件处理方法一共两个，一个是Button的单击事件，button.setOnClickListener()，一个是RadioGroup的切换事件，radioGroup.setOnCheckedChangeListener()，下面以Button为例简单介绍一下事件处理。  
要处理事件，首先要将与该事件有关的控件引入进来，比如要判断搜索内容是否为空，那么除了Button需要引入外（因为Button是触发事件的主体），还需要引入搜索内容的输入框。  
引入之后，button.setOnClickListener()中做处理，这里的button是一个变量名，记得换成自己定义的变量名。
```javascript
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //事件处理
        }
    });
```
onClick方法带的view参数是触发这个单击事件的控件的引用，在这个例子中，view就是触发事件的Button，在onClick()方法中做事件的处理，例如判断搜索内容是否为空：
```javascript
Button button = (Button) findViewById(R.id.btn0);
EditText searchContent = (EditText) findViewById(R.id.search);

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(TextUtils.isEmpty(searchContent.getText().toString())) {
            // 弹出Toast消息
        }
    }
});
```
#### 简单对话框的使用
Android 中最基本的对话框是AlertDialog，之所以说它最简单，是因为布局和使用的方法都很简单，布局是写好的。  
标题，通过 setTitle()方法设置；  
图标，通过 setIcon()方法设置；  
显示在中间的主要信息，通过setMessage()方法显示，等等。  

显示一个AlertDialog 的基本步骤如下：  
1. 创建AlertDialog.Builder对象；  
2. 调用上面的方法进行设置；  
3. 如果需要设置取消按钮，方法原型如下：
```javascript
setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击后的事件处理
            }
        })
```
确定按钮怎么设置自行查阅资料。  
4. 调用AlertDialog.Builder的create()方法创建AlertDialog对象，再调用AlertDialog对象的show()方法将对话框显示出来。

### 拓展知识
有兴趣的同学可以查阅Snackbar,TextInputLayout这两个控件的使用方法。

---

---

## 第六周任务
## Intent、Bundle的使用以及RecyclerView、ListView的应用
---
### 基础知识 
#### ListView的使用  
布局文件中写上
```javascript
    <ListView
        android:id="@+id/listView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```
即可创建一个空的ListView，然后在.java文件中填充数据。  
在.java文件中获得这个ListView后，使用Adapter为这个ListView填充数据，常用的Adapter有ArrayAdapter、SimpleAdapter。随着ListView中内容的丰富，这两种Adapter很难满足要求，因此现在一般使用自定义的Adapter来填充数据，也强烈建议同学们用自定义Adapter完成收藏夹的设计。自定义Adapter会在拓展知识中讲。
##### ArrayAdapter
最简单的Adapter，创建ArrayAdapter时需指定如下三个参数：  
Context : 代表了访问整个Android应用的接口。几乎创建所有组件都需要传入Context对象。  
textViewResourceId : 一个资源ID，代表一个TextView，该TextView组件将作为ArrayAdapter的列表项组件。  
数组或List : 负责为多个列表项提供数据  
```javascript
    String[] operations = {"text1", "text2", "text3",};
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.operation, operations);
    operationList.setAdapter(arrayAdapter);
```
创建完ArrayAdapter后，调用setAdapter方法即可填充数据。  
注意这里的textViewResourceId是一个layout，且**只含有一个TextView**，其他组件均不可存在，包括ConstraintLayout等布局组件。  
##### SimpleAdapter  
SimpleAdapter比ArrayAdapter强大很多，创建SimpleAdaper需要5个参数，第一个参数依然是Context。  
第二个参数 ： 该参数是一个List<? Extends Map<String, ?>>类型的集合对象，该集合中每个Map<String, ?>对象生成一个列表项。  
第三个参数 ： 该参数指定一个界面布局的ID。该界面布局指定每一个列表项的样式。  
第四个参数 ： 该参数是一个String[]类型的参数，该参数决定提取Map<String, ?>对象中哪些key对应的value来生成列表项。  
第五个参数 ： 该参数是一个int[]类型的参数，决定填充哪些组件。  

示例 ： 模拟一个图书清淡，一个map中有两个key，存放书名和价格，然后添加到list中。  
```javascript
    List<Map<String, Object>> data = new ArrayList<>();
    String[] booksName = new String[] {"book1", "book2", "book3"};
    String[] booksPrice = new String[] {"1.00", "1.50", "2.00"};
    for (int i = 0; i < 3; i++) {
        Map<String, Object> temp = new LinkedHashMap<>();
        temp.put("name", booksName[i]);
        temp.put("price", booksPrice[i]);
        data.add(temp);
    }
```
然后创建SimpleAdapter  
```javascript
    ListView listview = (ListView) findViewById(R.id.listview);
    SimpleAdapter simpleAdapter = new SimAdapter<String>(this, data, R.layout.item, new String[] {"name", "price"}, new int[] {R.id.name, R.id.price});
    listview.setAdapter(simpleAdapter);
```
R.id.name 与 R.id.price是在item布局文件中包含在ConstraintLayout中的两个textview，分别用语显示书名与价格，这个layout用于规定ListView中每个列表项的样式。SimpleAdapter中的第四个参数String数组与map的两个key对应，第五个参数int数组与这个layout中两个TextView的id相对应。注意String数组与int数组中的值要一一对应，在这个示例中，key为name的value填充到id为name的TextView中。  

#### ListView列表项的单击与长按  
方法原型如下  
```javascript
listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // 处理单击事件
    }
});

listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        // 处理长按事件
        return false;
    }
});
```
长按有返回值，具体区别可以自行查阅资料。注意在两个方法的参数中都有int i，long l这两个参数，i指的是这一项在列表中的位置，l指的是这一项的id，在ArrayAdapter和SimpleAdapter中，i和l是相等的，在另一种Adapter-CursorAdapter中，l指的是从数据库中取出的数据在数据库中的id值。  

#### ListView数据更新  
SimpleAdapter有一个notifyDataSetChanged()方法，当之前创建该SimpleAdapter的List发生改变时，调用该方法就可以刷新列表了。要特别注意的一点是，List不能指向新的内存地址，即不能list = new ArrayList<>(); 这样是不起作用的，只能调用它的remove()， add()等方法来改变数据集。  
示例  
```javascript
data.remove(0);
simpleAdapter.notifyDataSetChanged();
```
错误写法  
```javascript
data = new ArrayList<>();
simpleAdapter.notifyDataSetChanged();
```

#### RecyclerView的使用  
在project的build.gradle文件中添加maven依赖（这一步在前两周任务已做过，即加入aliyun的依赖）。  
在app的build.gradle文件中添加依赖：  
implementation 'com.android.support:cardview-v7:27.1.1'  
implementation 'com.android.support:recyclerview-v7:27.1.1'  
最后的版本号需要看自己的项目sdk版本号而设定。  
  
在布局文件加入  
```javascript
<android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

这样就创建了一个空的RecyclerView。在.jave文件中获得这个RecyclerView之后，选择显示方式。有以下几种。  
recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 类似ListView  
recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 类似GridView  
recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL)); // 线性宫格，类似瀑布流
    
使用Adapter为这个RecyclerView填充数据  
recyclerView.setAdapter(myAdapter);  
在RecyclerView中必须自定义实现RecyclerView.Adapter并为其提供数据集合，实现时必须遵循ViewHolder设计模式。ViewHolder通常出现在适配器中，为的是listview，recyclerview滚动时快速设置值，而不必每次都重新创建很多对象，从而提升性能。  
##### 自定义RecyclerView.ViewHolder  
public class MyViewHolder extends RecyclerView.ViewHolder  
使用一个SparseArray数组存储listItem中的子View  
```javascript
    private SparseArray<View> views;
    private View view;

    public MyViewHolder(Context _context, View _view, ViewGroup _viewGroup){
        super(_view);
        view = _view;
        views = new SparseArray<View>();
    }
```
获取ViewHolder实例  
```javascript
    public static MyViewHolder get(Context _context, ViewGroup _viewGroup, int _layoutId) {
        View _view = LayoutInflater.from(_context).inflate(_layoutId, _viewGroup, false);
        MyViewHolder holder = new MyViewHolder(_context, _view, _viewGroup);
        return holder;
    }
```
ViewHolder尚未将子View缓存到SparseArray数组中时，仍然需要通过findViewById()创建View对象，如果已缓存，直接返回即可。  
```javascript
    public <T extends View> T getView(int _viewId) {
        View _view = views.get(_viewId);
        if (_view == null) {
            // 创建view
            _view = view.findViewById(_viewId);
            // 将view存入views
            views.put(_viewId, _view);
        }
        return (T)_view;
    }
```

##### 自定义的RecyclerView.Adapter  
public abstract class MyRecyclerViewAdapter<T> extends RecyclerView.Adapter<MyViewHolder>  
其构造函数为  
public MyRecyclerViewAdapter(Context _context, int _layoutId, List<T> _data)  
  
Adapter扮演着两个角色，一是根据不同ViewType创建与之相应的Item-Layout，二是访问数据集合并将数据绑定到正确的View上。因此需要重写一下两个函数  
public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)创建Item视图，并返回相应的ViewHolder。  
public void onBindViewHolder(final MyViewHolder holder, int position)绑定数据到正确的Item视图上。  
```javascript
@Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(context, parent, layoutId);
        return holder;
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, int position) {
        convert(holder, data.get(position)); // convert函数需要重写，下面会讲
    }
```
MyRecyclerViewAdapter类中需要声明抽象方法convert  
public abstract void convert(MyViewHolder holder, T t);  
在定义MyRecyclerViewAdapter时再实现它  
```javascript
final MyRecyclerViewAdapter myAdapter = new MyRecyclerViewAdapter<Collection>(MainActivity.this, R.layout.item, data) {
    @Override
    public void convert(MyViewHolder holder, Collection s) {
        // Colloction是自定义的一个类，封装了数据信息，也可以直接将数据做成一个Map，那么这里就是Map<String, Object>
        TextView name = holder.getView(R.id.recipeName);
        name.setText(s.getName().toString());
        Button first = holder.getView(R.id.img);
        first.setText(s.getFirst().toString());
    }
};
```
RecyclerView没有OnItemClickListener方法，需要在Adapter中实现。方法为：在Adapter中设置一个监听器，当itemView被点击时，调用该监听器并且将itemView的position作为参数传递出去。  
首先添加接口和函数。  
```javascript
public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }
```
在onBindViewHolder()中添加  
```javascript
    if (onItemClickListener != null) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 onItemClickListener.onClick(holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemClickListener.onLongClick(holder.getAdapterPosition());
                return false;
            }
        });
    }
```
这样就可以使用setOnItemClickListener()方法了。  

#### 为RecyclerView添加动画  
本次实验使用网上的库，如果自己实现了好看的动画，在实验报告中说明，有加分。  
添加依赖  
implementation 'jp.wasabeef:recyclerview-animators:2.3.0'  
implementation 'com.android.support:support-core-utils:27.1.1'  
版本号随项目版本而变，自定。  
  
将recyclerView.setAdapter(myRecyclerViewAdapter);  
改成  
```javascript
ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(myAdapter);
scaleInAnimationAdapter.setDuration(1000);
recyclerView.setAdapter((scaleInAnimationAdapter));
recyclerView.setItemAnimator(new OvershootInLeftAnimator());
```
这样就添加了动画效果。在 github.com/wasabeef/recyclerview-animators 上有动画的详细说明，可以自行更换。(更换不算自己实现动画)  

#### 去掉标题栏  
去掉标题栏方法很多，这里举一种。Android Studio创建项目时默认的theme是  
```javascript
android:theme="@style/AppTheme"
```
这个是在AndroidManifest文件中定义的，可以去看看。它的定义是  
```javascript
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>
```
修改parent即可  
```javascript
<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
```
#### 星星图标的切换  
星星切换的难点在于如何得知星星此时是空心还是实心，这个也有多种做法。这里介绍一种简单的。  
每个View都可以设置tag，通过tag可以用来判断该View现在的状态。在初始化的时候，将tag设置为0，标记此时为空心星星，如果星星被点击了，并且tag为0，那么将图片换成实心的星星，然后设置tag为1；如果tag为1，那么将图片换成空心的星星，然后设置tag为0。建议在.java文件中给需要的view设置tag。  

#### FloatingActionBar  
添加依赖  
implementation 'com.android.support:design:27.1.1'  
版本号自定。  
设置布局文件  
```javascript
    <android.support.design.widget.FloatingActionButton
        android:id="@+id/btn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@mipmap/colletions"
        android:backgroundTint="@color/colorWhite"
        android:backgroundTintMode="src_atop"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        android:layout_margin="25dp" />
```
切换图片时使用setImageResource()。  

### 拓展知识   
ListView的自定义Adapter  
前面介绍的ArrayAdapter和SimpleAdapter都有一定的局限性，SimpleAdapter较ArrayAdapter要好一些，但还是不够灵活，假如某些列表项需要有一些特性，或者列表项中某些控件需要设置监听器，就不够用了。因此，强烈建议大家一开始就习惯自定义Adapter来适配自己的列表，只在某些简单的情况下才使用前面的两种Adapter。  
自定义的Adapter需要集成BaseAdapter  
```javascript
public class MyListViewAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
```
上面列出的四个方法是必须重写的四个方法，下面一一介绍这四个方法。  
int getCount();获得数据项列表的长度，也就是一共有多少个数据项。  
Object getItem(int i); //获得一个数据项。  
long getItemId(int i); //获得数据项的位置。  
View getView(int i, View view, ViewGroup viewGroup); //获得数据项的布局样式，最重要的一个方法。  
自定义Adapter需要提供一个数据列表才能填充数据，一般是一个List类型，以图书列表为例，可以献给列表项创建一个类Book，然后将List传入Adapter中作为数据提供的列表。  
```javascript
public class Book {
    private String bookName;
    private String bookPrice;
    public Book(String bookName, String bookPrice) {
        this.bookName = bookName;
        this.bookPrice = bookPrice;
    }
    ... // get & set 方法
}
```
```javascript
public class MyAdapter extends BaseAdapter {
    private List<Book> list;

    public MyAdapter(list<Book> list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }
    ... // 其余方法
}
```
依照刚才的想法重写完，截下来是最重要的getView()方法，首先解释一个三个参数的含义  
public View getView(int i, View view, ViewGroup viewGroup)  
i指的是当前是在加载第几项的列表项  
viewGroup是列表项View的父视图，调整列表项的宽高用的  
view指的是一个列表项的视图，我们需要给view一个布局，然后在布局中放置我们需要的内容  
```javascript
@Override
public View getView(int i, View view, ViewGroup viewGroup) {
    // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
    view = LayoutInfalter.from(context).inflate(R.layout.item, null);
    // 获得布局中显示书名和价格的两个TextView
    TextView bookName = (TextView) view.findViewById(R.id.name);
    TextView bookPrice = (TextView) view.findViewById(R.id.price);
    // 从数据列表中取出对应的对象，然后赋值给他们
    bookName.setText(list.get(i).getBookName());
    bookPrice.setText(list.get(i).getBookPrice());
    // 将这个处理好的view返回
    return view;
}
```
这是getView()方法最基本的写法。每次从屏幕外滚进来一个新的项就要再加载一次布局。其实ListView每次有新的一项滚入就会滚出另一项，这时候view是有内容的，但是是旧内容，因此只需要改变一下view的内容然后返回它就可以了，不需要再去加载一次布局。  
```javascript
@Override
public View getView(int i, View view, ViewGroup viewGroup) {
    // 当view为空时才加载布局，否则，直接修改内容
    if (view == null) {
        // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
        view = LayoutInfalter.from(context).inflate(R.layout.item, null);
    }
    // 获得布局中显示书名和价格的两个TextView
    TextView bookName = (TextView) view.findViewById(R.id.name);
    TextView bookPrice = (TextView) view.findViewById(R.id.price);
    // 从数据列表中取出对应的对象，然后赋值给他们
    bookName.setText(list.get(i).getBookName());
    bookPrice.setText(list.get(i).getBookPrice());
    // 将这个处理好的view返回
    return view;
```
这样写可以减少一些重复的加载布局操作，提高效率。但是每次findViewById()也是一件很麻烦的事情，如果控件一多，也会降低ListView的效率。因此，使用setTag的方法和新建一个ViewHolder类来提高这部分的效率。  
```javascript
@Override
public View getView(int i, View view, ViewGroup viewGroup) {
    // 新声明一个View变量和ViewHoleder变量,ViewHolder类在下面定义。
    View convertView;
    ViewHolder viewHolder;
    // 当view为空时才加载布局，否则，直接修改内容
    if (view == null) {
        // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
        view = LayoutInfalter.from(context).inflate(R.layout.item, null);
        viewHolder = new ViewHolder();
        viewHolder.bookName = (TextView) view.findViewById(R.id.name);
        viewHolder.bookPrice = (TextView) view.findViewById(R.id.price);
        convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
    } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
        convertView = view；
        viewHolder = (ViewHolder) convertView.getTag();
    }   
    // 从viewHolder中取出对应的对象，然后赋值给他们
    viewHolder.bookName.setText(list.get(i).getBookName());
    viewHolder.bookPrice.setText(list.get(i).getBookPrice());
    // 将这个处理好的view返回
    return convertView;
}

private class ViewHolder {
    public TextView bookName;
    public TextView bookPrice;
}
```
这样写ListView效率就比较高。  
最终版的Adapter如下。  
```javascript
public class myListViewAdapter extends BaseAdapter {
    private List<Book> list;

    public MyAdapter(list<Book> list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 新声明一个View变量和ViewHoleder变量,ViewHolder类在下面定义。
        View convertView;
        ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (view == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            view = LayoutInfalter.from(context).inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.bookName = (TextView) view.findViewById(R.id.name);
            viewHolder.bookPrice = (TextView) view.findViewById(R.id.price);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            convertView = view；
            viewHolder = (ViewHolder) convertView.getTag();
        }   
        // 从viewHolder中取出对应的对象，然后赋值给他们
        viewHolder.bookName.setText(list.get(i).getBookName());
        viewHolder.bookPrice.setText(list.get(i).getBookPrice());
        // 将这个处理好的view返回
        return convertView;
    }

    private class ViewHolder {
        public TextView bookName;
        public TextView bookPrice;
    }
}
```

### 一些可以参考的tips  
* 关于ListView数据项更新，可以在Activity中修改原list(即调用remove方法)，然后再次传入adapter的一个方法中，比如自定义一个refresh方法，然后在这个方法中对adapter中的list进行重赋值，然后调用notifyDataSetChanged()方法。代码...不贴。
* 关于RecyclerView数据项更新，和ListView更新类似。由于点击事件实际上是在Adapter中处理的，可以直接在Adapter的onBindViewHolder方法中直接处理更新问题。之前在绑定点击事件处理时holder.getAdapterPosition()这一项实际上是得到被点击的那一项的位置，因此可以直接从data项中删除它，然后调用notifyItemRemoved(holder.getAdapterPosition())。代码...依旧不贴。
* 关于长按事件处理。返回值为true和false时得到的结果是不同的。在本次实验中，若返回值为false，应用很容易就崩了，可以自己尝试一下，然后发现原因。
* 关于页面跳转。建议使用startActivityForResult方法。然后利用onActivityResult方法处理返回的结果。当然也可以用别的方法，只要最后结果是对的就可以。（何为结果对，这一点在要求里写了）具体实现...依旧不贴。
* 关于新建一个Collection类。因为在定义ListView的Adapter时实际上是用来处理一个类，因此将所有数据都封装成一个类，然后页面跳转时需要将这个类序列化后放在bundle里，然后再将这个bundle放在intent中，所以这个类在声明时注意要implements Serializable。
* 关于在收藏夹点进详情页面然后点击收藏后点击返回，注意此时收藏夹应该多出新的一项，然后再次点击进入详情页面后，不点击收藏再点击返回按钮，收藏夹不应该多出新的一项。
* 关于FloatingActionButton的切换事件。由于点击这个按钮，会在RecyclerView和ListView之间切换，可以将这两个控件写在同一个布局文件中，通过在.java文件中调用setVisibility方法来使得一个显示，一个不显示。
>>>>>>> be34c25d3e8e34fc21f3efa0c3d5c44c67b40b1b
