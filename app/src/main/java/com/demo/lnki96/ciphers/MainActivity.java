package com.demo.lnki96.ciphers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lnki9 on 2016/9/21 0021.
 */

public class MainActivity extends AppCompatActivity {
    final int CAESAR_ORDER = 0, VIGENERE_ORDER = 1, AUTOKEY_ORDER = 2, KEYWORD_ORDER = 3, COLUMN_PERMUTATION_ORDER = 4, PLAYFAIR_ORDER = 5, RC4_ORDER = 6, DES_ORDER = 7, RSA_ORDER = 8, CRYPT_ORDER = 9, SOCKET_ORDER = 10, FILE_ORDER = 11;
    final int FILE_SELECT = 256;
    final boolean[][] disabledMap = {
            {false, false, false, false, false, false, false, false, false, false, false, false},//Socket开关
            {false, false, false, false, false, false, false, false, false, false, false, true},//DES开关
            {false, false, false, false, false, false, false, false, false, false, true, true},//非DES开关
            {false, false, false, false, false, false, false, false, false, true, true, true}//RC4及未选择开关
    };
    final int SOCKET_CONNECT = 0, SOCKET_TRANSFER = 1, SOCKET_QUIT = 2;
    boolean[] toggles = {false, false, false, false, false, false, false, false, false, false, false, false, false},
            togglesPrev = toggles.clone(),
            disabled = disabledMap[3].clone();
    CoordinatorLayout root;
    ViewGroup cipherList, modeList;
    List<CardView> switchList = new ArrayList<>();
    TextInputLayout keyLengthInput, aInput, keyInput, plaintextInput, privateKeyInput, ciphertextInput, ipAddrInput, filePathInput;
    FloatingActionButton cryptBtn;
    Bundle infoPool = new Bundle();
    View openFileInclude, modeListInclude, passwordInclude, dhLayout;
    //    ServerAsync server;
//    ClientThread client;
    KeyPair keyPair;
    String plaintext, key, privateKey, ciphertext, ipAddr, filePath;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case SOCKET_CONNECT:
                    keyInput.getEditText().setText(bundle.getString("p"));
                    privateKeyInput.getEditText().setText(bundle.getString("g"));
                    aInput.getEditText().setText(bundle.getString((toggles[CRYPT_ORDER]) ? "B" : "A"));
                    break;
                case SOCKET_TRANSFER:
                    if (toggles[FILE_ORDER]) {
                        Snackbar.make(root, ((toggles[CRYPT_ORDER]) ? "Receiving" : "Sending") + " file " + ((bundle.getBoolean("isSuccess")) ? "finished." : "failed."), Snackbar.LENGTH_LONG).show();
                    } else {
                        if (toggles[CRYPT_ORDER]) {
                            plaintextInput.getEditText().setText(bundle.getString("ciphertext"));
                            ciphertextInput.getEditText().setText(bundle.getString("plaintext"));
                        } else {
                            ciphertextInput.getEditText().setText(bundle.getString("ciphertext"));
                        }
                    }
                    break;
                case SOCKET_QUIT:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    View.OnClickListener cipherListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean toggle = false;
            switch (v.getId()) {
                case R.id.caesear:
                    toggle = toggles[CAESAR_ORDER] = !toggles[CAESAR_ORDER];
                    break;
                case R.id.vigenere:
                    toggle = toggles[VIGENERE_ORDER] = !toggles[VIGENERE_ORDER];
                    break;
                case R.id.keyword:
                    toggle = toggles[KEYWORD_ORDER] = !toggles[KEYWORD_ORDER];
                    break;
                case R.id.autokey:
                    toggle = toggles[AUTOKEY_ORDER] = !toggles[AUTOKEY_ORDER];
                    break;
                case R.id.column_permutation:
                    toggle = toggles[COLUMN_PERMUTATION_ORDER] = !toggles[COLUMN_PERMUTATION_ORDER];
                    break;
                case R.id.playfair:
                    toggle = toggles[PLAYFAIR_ORDER] = !toggles[PLAYFAIR_ORDER];
                    break;
                case R.id.rc4:
                    toggle = toggles[RC4_ORDER] = !toggles[RC4_ORDER];
                    break;
                case R.id.des:
                    toggle = toggles[DES_ORDER] = !toggles[DES_ORDER];
                    break;
                case R.id.rsa:
                    toggle = toggles[RSA_ORDER] = !toggles[RSA_ORDER];
                    if (toggles[RSA_ORDER]) {
                        keyInput.setHint(getString(R.string.public_key));
                        privateKeyInput.setHint(getString(R.string.private_key));
                        animWidth(keyLengthInput, 0, 112, keyLengthInput, View.VISIBLE);
                        animWidth(keyInput, (togglesPrev[SOCKET_ORDER]) ? 205 : 411f, 149f, null, 0);
                        animWidth(privateKeyInput, (togglesPrev[SOCKET_ORDER]) ? 205 : 149f, 149f, privateKeyInput, View.VISIBLE);
                    } else {
                        keyInput.setHint(getString(R.string.key));
                        animWidth(keyLengthInput, 112, 0, keyLengthInput, View.GONE);
                        animWidth(keyInput, 149f, 411f, privateKeyInput, View.GONE);
                    }
                    break;
                default:
                    break;
            }
            boolean isSelected = false;
            if (toggle) {
                int i;
                for (i = CAESAR_ORDER; i <= RSA_ORDER; i++)
                    if (v.getId() != switchList.get(i).getId() && toggles[i]) {
                        toggles[i] = false;
                        isSelected = true;
                        break;
                    }
                if (!isSelected) {
                    animHeight(modeListInclude, 0, infoPool.getInt("modeListIncludeHeight"), modeListInclude, View.VISIBLE);
                    animHeight(plaintextInput, 0, infoPool.getInt("plaintextInputHeight"), plaintextInput, View.VISIBLE);
                    animHeight(passwordInclude, 0, infoPool.getInt("passwordIncludeHeight"), passwordInclude, View.VISIBLE);
                    animHeight(ciphertextInput, 0, infoPool.getInt("ciphertextInputHeight"), ciphertextInput, View.VISIBLE);
                }
            } else {
                animHeight(modeListInclude, infoPool.getInt("modeListIncludeHeight"), 0, modeListInclude, View.GONE);
                animHeight(plaintextInput, infoPool.getInt("plaintextInputHeight"), 0, plaintextInput, View.GONE);
                animHeight(passwordInclude, infoPool.getInt("passwordIncludeHeight"), 0, passwordInclude, View.GONE);
                animHeight(ciphertextInput, infoPool.getInt("ciphertextInputHeight"), 0, ciphertextInput, View.GONE);
            }
            togglesControl();
        }
    };

    View.OnClickListener crypt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (toggles[SOCKET_ORDER]) {
                if (toggles[FILE_ORDER])
//                    Snackbar.make(root, ((toggles[CRYPT_ORDER]) ? "Receiving" : "Sending") + " file finished.", Snackbar.LENGTH_LONG).show();
                    if (toggles[CRYPT_ORDER]) {//服务器
//                    if (server == null) {
//                        server = new ServerAsync();
//                        server.execute();
//                    }
                    } else {//客户端
//                    if (client == null) {
//                        client = new ClientThread();
//                        client.run();
//                    }
                    }
            } else if (!(key = keyInput.getEditText().getText().toString()).equals("") || !toggles[CRYPT_ORDER]) {
                plaintext = plaintextInput.getEditText().getText().toString();
                if (toggles[CAESAR_ORDER]) {
                    if (!toggles[CRYPT_ORDER])
                        ciphertext = Caesear.Encryption(plaintext, Integer.parseInt(key));
                    else ciphertext = Caesear.Decrypt(plaintext, Integer.parseInt(key));
                } else if (toggles[VIGENERE_ORDER]) {
                    if (!toggles[CRYPT_ORDER])
                        ciphertext = new String(Vigenere.jiami(plaintext.length(), key.length(), plaintext.toCharArray(), key.toCharArray(), Vigenere.creatVigenereTable()));
                    else
                        ciphertext = new String(Vigenere.jiemi(plaintext.length(), key.length(), plaintext.toCharArray(), key.toCharArray(), Vigenere.creatVigenereTable()));
                } else if (toggles[KEYWORD_ORDER]) {
                    if (!toggles[CRYPT_ORDER])
                        ciphertext = Keyword.Encryption(plaintext, key);
                    else ciphertext = Keyword.Decrypt(plaintext, key);
                } else if (toggles[AUTOKEY_ORDER]) {
                    if (!toggles[CRYPT_ORDER])
                        ciphertext = new String(Autokey.jiami(plaintext.length(), key.length(), plaintext.toCharArray(), key.toCharArray(), Vigenere.creatVigenereTable()));
                    else
                        ciphertext = new String(Autokey.jiemi(plaintext.length(), key.length(), plaintext.toCharArray(), key.toCharArray(), Vigenere.creatVigenereTable()));
                } else if (toggles[COLUMN_PERMUTATION_ORDER]) {
                    if (!toggles[CRYPT_ORDER])
                        ciphertext = ColumnPermutation.MyCp_Encryption(plaintext, key);
                    else
                        ciphertext = ColumnPermutation.MyCp_Decryption(plaintext, key);
                } else if (toggles[PLAYFAIR_ORDER]) {
                    if (!toggles[CRYPT_ORDER])
                        ciphertext = Playfair.Encrypt(plaintext, key);
                    else
                        ciphertext = Playfair.Decrypt(plaintext, key);
                } else if (toggles[RC4_ORDER]) {
                    ciphertext = com.demo.lnki96.ciphers.RC4.MyRC4(plaintext, key);
                } else if (toggles[DES_ORDER]) {
                    CryptWithDES_H des = new CryptWithDES_H(key);
                    try {
                        ciphertext = new String(des.DesEncrypt(plaintext.getBytes("ISO-8859-1"), (toggles[CRYPT_ORDER]) ? 1 : 0), "ISO-8859-1");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else if (toggles[RSA_ORDER]) {
                    try {
                        if (!toggles[CRYPT_ORDER]) {
                            keyPair = CryptWithRSA_H.generateRSAKeyPair(Integer.parseInt(keyLengthInput.getEditText().getText().toString()));
                            BigInteger[] keyElements = CryptWithRSA_H.getRSAElements(keyPair);
                            keyInput.getEditText().setText(String.format(Locale.CHINA, "%o", keyElements[1]));
                            privateKeyInput.getEditText().setText(String.format(Locale.CHINA, "%o", keyElements[2]));
                            ciphertext = CryptWithRSA_H.encrypt(plaintext.getBytes("ISO-8859-1"), (RSAPublicKey) keyPair.getPublic()).toString();
                        } else
                            ciphertext = new String(CryptWithRSA_H.decrypt(new BigInteger(plaintext), (RSAPrivateKey) keyPair.getPrivate()), "ISO-8859-1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } //else ciphertext = "";
                ciphertextInput.getEditText().setText(ciphertext);
            }
        }
    };

    View.OnClickListener modeListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.crypt:
                    toggles[CRYPT_ORDER] = !toggles[CRYPT_ORDER];
                    if (toggles[SOCKET_ORDER])
                        if (!toggles[CRYPT_ORDER])
                            ipAddrInput.getEditText().setText("");
                        else {
                            ipAddrInput.getEditText().setText(ipString(((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress()));
                            ipAddrInput.getEditText().setSelection(ipAddrInput.getEditText().length());
                        }
                    break;
                case R.id.file:
                    toggles[FILE_ORDER] = !toggles[FILE_ORDER];
                    if (toggles[FILE_ORDER]) {//文件模式
                        openFileInclude.measure(0, 0);
                        animHeight(openFileInclude, 0, infoPool.getInt("openFileIncludeHeight"), openFileInclude, View.VISIBLE);
                        animHeight(plaintextInput, infoPool.getInt("plaintextInputHeight"), 0, plaintextInput, View.GONE);
                        animHeight(ciphertextInput, infoPool.getInt("ciphertextInputHeight"), 0, ciphertextInput, View.GONE);
                    } else {//字节流模式
                        openFileInclude.measure(0, 0);
                        animHeight(openFileInclude, infoPool.getInt("openFileIncludeHeight"), 0, openFileInclude, View.GONE);
                        animHeight(plaintextInput, 0, infoPool.getInt("plaintextInputHeight"), plaintextInput, View.VISIBLE);
                        animHeight(ciphertextInput, 0, infoPool.getInt("ciphertextInputHeight"), ciphertextInput, View.VISIBLE);
                    }
                    break;
                case R.id.socket:
                    toggles[SOCKET_ORDER] = !toggles[SOCKET_ORDER];
                    if (toggles[SOCKET_ORDER]) {
                        animWidth(ipAddrInput, 0, 124, ipAddrInput, View.VISIBLE);
                        animWidth(keyInput, 411, 205, aInput, View.VISIBLE);
                        animHeight(dhLayout, 0, infoPool.getInt("dhLayoutHeight"), dhLayout, View.VISIBLE);
                        keyInput.setHint(getString(R.string.p));
                        animWidth(privateKeyInput, 205, 205, privateKeyInput, View.VISIBLE);
                        privateKeyInput.setHint(getString(R.string.g));
                    } else {
                        ipAddrInput.getEditText().setText("");
                        animWidth(ipAddrInput, 124, 0, ipAddrInput, View.GONE);
                        animWidth(keyInput, 205, 411, aInput, View.GONE);
                        animHeight(dhLayout, infoPool.getInt("dhLayoutHeight"), 0, dhLayout, View.GONE);
                        keyInput.setHint(getString(R.string.key));
                        animWidth(privateKeyInput, 205, 205, privateKeyInput, View.GONE);
                        if (togglesPrev[FILE_ORDER]) {
                            animHeight(openFileInclude, infoPool.getInt("openFileIncludeHeight"), 0, openFileInclude, View.GONE);
                            animHeight(plaintextInput, 0, infoPool.getInt("plaintextInputHeight"), plaintextInput, View.VISIBLE);
                            animHeight(ciphertextInput, 0, infoPool.getInt("ciphertextInputHeight"), ciphertextInput, View.VISIBLE);
                        }
                    }
                    break;
                default:
                    break;
            }
            togglesControl();
        }
    };

    View.OnClickListener open = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Choose a file"), FILE_SELECT);
            } catch (android.content.ActivityNotFoundException ignored) {

            }
        }
    };

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT && resultCode == Activity.RESULT_OK) {
            filePathInput.getEditText().setText(Uri.decode(data.getData().toString()));
            filePathInput.requestLayout();
            filePathInput.getEditText().setSelection(filePathInput.getEditText().length());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = (CoordinatorLayout) findViewById(R.id.root);
        openFileInclude = findViewById(R.id.open_file_include);
        openFileInclude.measure(0, 0);
        infoPool.putInt("openFileIncludeHeight", px2dp(this, openFileInclude.getMeasuredHeight()));
        modeListInclude = findViewById(R.id.mode_list_include);
        modeListInclude.measure(0, 0);
        infoPool.putInt("modeListIncludeHeight", px2dp(this, modeListInclude.getMeasuredHeight()));
        passwordInclude = findViewById(R.id.password_include);
        passwordInclude.measure(0, 0);
        infoPool.putInt("passwordIncludeHeight", px2dp(this, passwordInclude.getMeasuredHeight()));
        dhLayout = findViewById(R.id.dh_layout);
        dhLayout.measure(0, 0);
        infoPool.putInt("dhLayoutHeight", px2dp(this, dhLayout.getMeasuredHeight()));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        cipherList = (ViewGroup) findViewById(R.id.cipher_list);
        modeList = (ViewGroup) findViewById(R.id.mode_list);

        for (int i = 0; i < cipherList.getChildCount(); i++) {
            switchList.add((CardView) cipherList.getChildAt(i));
            switchList.get(switchList.size() - 1).setOnClickListener(cipherListListener);
        }
        for (int i = 0; i < modeList.getChildCount() - 1; i++) {
            switchList.add((CardView) modeList.getChildAt(i));
            switchList.get(switchList.size() - 1).setOnClickListener(modeListListener);
            switchList.get(switchList.size() - 1).setClickable(false);
        }
        findViewById(R.id.open).setOnClickListener(open);

        cryptBtn = (FloatingActionButton) findViewById(R.id.send);
        cryptBtn.setOnClickListener(crypt);

        plaintextInput = (TextInputLayout) findViewById(R.id.plaintext);
        plaintextInput.measure(0, 0);
        infoPool.putInt("plaintextInputHeight", px2dp(this, plaintextInput.getMeasuredHeight()));
        keyLengthInput = (TextInputLayout) findViewById(R.id.key_length);
        keyInput = (TextInputLayout) findViewById(R.id.key);
        privateKeyInput = (TextInputLayout) findViewById(R.id.public_key);
        aInput = (TextInputLayout) findViewById(R.id.a);
        ciphertextInput = (TextInputLayout) findViewById(R.id.ciphertext);
        ciphertextInput.measure(0, 0);
        infoPool.putInt("ciphertextInputHeight", px2dp(this, ciphertextInput.getMeasuredHeight()));
        ipAddrInput = (TextInputLayout) findViewById(R.id.ip_addr);
        filePathInput = (TextInputLayout) findViewById(R.id.file_path);

        ipAddr = ipString(((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress());
    }

    void togglesControl() {
        if (toggles[DES_ORDER]) {
            disabled = disabledMap[1];
            if (toggles[SOCKET_ORDER])
                disabled = disabledMap[0];
        } else
            disabled = disabledMap[3];
        for (int i = CAESAR_ORDER; i <= RSA_ORDER; i++)
            if (i != DES_ORDER && i != RC4_ORDER && toggles[i]) {
                disabled = disabledMap[2];
                break;
            }

        for (int i = 0; i < switchList.size(); i++) {
            if (disabled[i] && toggles[i])
                toggles[i] = false;
        }
        updateUi();
    }

    void updateUi() {
        for (int i = 0; i < switchList.size(); i++) {
            if (disabled[i]) {
                switchList.get(i).setCardBackgroundColor(ContextCompat.getColor(this, R.color.disabled));
                switchList.get(i).setClickable(false);
            } else {
                switchList.get(i).setClickable(true);
                if (i == SOCKET_ORDER)
                    switchList.get(i).setCardBackgroundColor(ContextCompat.getColor(this, (toggles[i]) ? R.color.colorAccent : R.color.switcher));
                else if (i >= CAESAR_ORDER && i <= PLAYFAIR_ORDER)
                    switchList.get(i).setCardBackgroundColor(ContextCompat.getColor(this, (toggles[i]) ? R.color.colorAccent : R.color.classicalCipher));
                else if (i >= RC4_ORDER && i <= RSA_ORDER)
                    switchList.get(i).setCardBackgroundColor(ContextCompat.getColor(this, (toggles[i]) ? R.color.colorAccent : R.color.modernCipher));
                else if (i == CRYPT_ORDER) {
                    switchList.get(i).setCardBackgroundColor(ContextCompat.getColor(this, (toggles[i]) ? R.color.colorAccent : R.color.switcher));
                    if (toggles[SOCKET_ORDER])
                        ((TextView) switchList.get(i).getChildAt(0)).setText(getString((toggles[i]) ? R.string.server : R.string.client));
                    else
                        ((TextView) switchList.get(i).getChildAt(0)).setText(getString((toggles[i]) ? R.string.decrypt : R.string.crypt));
                } else if (i == FILE_ORDER) {
                    switchList.get(i).setCardBackgroundColor(ContextCompat.getColor(this, (toggles[i]) ? R.color.colorAccent : R.color.switcher));
                    ((TextView) switchList.get(i).getChildAt(0)).setText(getString((toggles[i]) ? R.string.file : R.string.bytes));
                }
            }
        }

        if (!toggles[RSA_ORDER] && togglesPrev[RSA_ORDER]) {
            keyInput.setHint(getString(R.string.key));
            animWidth(keyLengthInput, 112, 0, keyLengthInput, View.GONE);
            animWidth(keyInput, 149f, 411f, privateKeyInput, View.GONE);
        }
        if (!toggles[DES_ORDER] && togglesPrev[SOCKET_ORDER]) {
            ipAddrInput.getEditText().setText("");
            animWidth(ipAddrInput, 124, 0, ipAddrInput, View.GONE);
            animHeight(dhLayout, infoPool.getInt("dhLayoutHeight"), 0, dhLayout, View.GONE);
            if (togglesPrev[FILE_ORDER]) {
                animHeight(openFileInclude, infoPool.getInt("openFileIncludeHeight"), 0, openFileInclude, View.GONE);
                for (int i = 0; i <= RSA_ORDER; i++)
                    if (toggles[i]) {
                        animHeight(plaintextInput, 0, infoPool.getInt("plaintextInputHeight"), plaintextInput, View.VISIBLE);
                        animHeight(ciphertextInput, 0, infoPool.getInt("ciphertextInputHeight"), ciphertextInput, View.VISIBLE);
                        break;
                    }
            }
            if (!toggles[RSA_ORDER]) {
                animWidth(keyInput, 205, 411, aInput, View.GONE);
                keyInput.setHint(getString(R.string.key));
                animWidth(privateKeyInput, 205, 205, privateKeyInput, View.GONE);
            }
        }
        if (toggles[SOCKET_ORDER])
            aInput.setHint(getString((toggles[CRYPT_ORDER]) ? R.string.b : R.string.a));
        plaintextInput.setHint((toggles[CRYPT_ORDER]) ? getString(R.string.ciphertext) : getString(R.string.plaintext));
        ciphertextInput.setHint((toggles[CRYPT_ORDER]) ? getString(R.string.plaintext) : getString(R.string.ciphertext));

        if (toggles[CAESAR_ORDER])
            keyInput.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        else
            keyInput.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        keyInput.getEditText().setSelection(keyInput.getEditText().length());
        togglesPrev = toggles.clone();
    }

    void animWidth(final View v, float start, float end, @Nullable final View show, final int visibility) {
        final WidthAnimator width;
        width = (WidthAnimator) new WidthAnimator().setDuration(200);
        width.setIntValues(dp2px(this, start), dp2px(this, end));
        width.setInterpolator(new DecelerateInterpolator());
        width.setTarget(v);
        if (show != null) {

            width.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    if (visibility == View.VISIBLE)
                        show.setVisibility(visibility);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (visibility == View.GONE || visibility == View.INVISIBLE) {
                        show.setVisibility(visibility);
                        width.removeAllListeners();
                    }
                    if (toggles[CRYPT_ORDER] && toggles[SOCKET_ORDER] && v == ipAddrInput) {
                        ipAddrInput.getEditText().setText(ipString(((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress()));
                        ipAddrInput.getEditText().setSelection(ipAddrInput.getEditText().length());
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        width.start();
    }

    void animHeight(final View v, float start, float end, @Nullable final View show, final int visibility) {
        final HeightAnimator height;
        height = (HeightAnimator) new HeightAnimator().setDuration(200);
        height.setIntValues(dp2px(this, start), dp2px(this, end));
        height.setInterpolator(new DecelerateInterpolator());
        height.setTarget(v);
        if (show != null) {
            height.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    if (visibility == View.VISIBLE)
                        v.setVisibility(visibility);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (visibility == View.GONE || visibility == View.INVISIBLE) {
                        show.setVisibility(visibility);
                        height.removeAllListeners();
                    } else// if (v instanceof TextInputLayout)
                        v.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        height.start();
    }

    String ipString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + ((i >> 24) & 0xFF);
    }

    class WidthAnimator extends ValueAnimator {
        View target;

        @Override
        public void setTarget(Object target) {
            this.target = (View) target;
            super.setTarget(target);
        }

        @Override
        public void start() {
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    target.getLayoutParams().width = (int) WidthAnimator.this.getAnimatedValue();
                    target.requestLayout();
                }
            });
            super.start();
        }
    }

    class HeightAnimator extends ValueAnimator {
        View target;

        @Override
        public void setTarget(Object target) {
            this.target = (View) target;
            super.setTarget(target);
        }

        @Override
        public void start() {
            addUpdateListener(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    target.getLayoutParams().height = (int) HeightAnimator.this.getAnimatedValue();
                    target.requestLayout();
                }
            });
            super.start();
        }
    }
}