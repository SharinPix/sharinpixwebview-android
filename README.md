# sharinpixwebview-android

An android library for allowing users to use the SharinPix application(app.sharinpix.com) in a custom WebView that allow the user to capture images using camera and upload images using file picker.

## Steps for Installing SharinPixWebView Library

In settings.gradle include this line:
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        .
        .
        .
        mavenCentral()
    }
}
```

In build.gradle for your project module include this line:
```
dependencies {
    implementation 'com.sharinpix.webview:sharinpixwebview-android:LATEST_VERSION'
}
```

## Usage

```
public class MainActivity extends AppCompatActivity {
   private ActivityMainBinding binding;
   private SharinPixWebView sharinPixWebView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       binding = ActivityMainBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
       this.sharinPixWebView = new SharinPixWebView(MainActivity.this, R.id.webView, "https://app.sharinpix.com/?token=XXXXX");
       this.sharinPixWebView.setOnResultListener(new SharinPixWebView.OnResultListener() {
           @Override
           public void onResult(JSONObject jsonObject) throws JSONException {
               Toast.makeText(getApplicationContext(), jsonObject.getString("name"), Toast.LENGTH_LONG).show();
           }
       });
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       this.sharinPixWebView.onActivityResult(requestCode, resultCode, data);
   }
}
```

## Overriding the `onActivityFunction` Function

```
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
   super.onActivityResult(requestCode, resultCode, data);
   this.sharinPixWebView.onActivityResult(requestCode, resultCode, data);
}
```

## Creating an Instance of SharinPixWebView

Parameters:
- Activity: the main activity from which your WebView
- WebView ID
- URL

```
this.sharinPixWebView = new SharinPixWebView(MainActivity.this, R.id.webView, "https://app.sharinpix.com/?token=XXXXX");
```

## Implementing the SharinPixWebView Listener

```
this.sharinPixWebView.setOnResultListener(new SharinPixWebView.OnResultListener() {
   @Override
   public void onResult(JSONObject jsonObject) throws JSONException {
       Toast.makeText(getApplicationContext(), jsonObject.getString("name"), Toast.LENGTH_LONG).show();
   }
});
```
