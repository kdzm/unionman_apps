package com.um.launcher.view.setting;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

import android.content.Context;
import android.content.res.Resources;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkUtils;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Eap;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.um.launcher.R;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiConfigController implements TextWatcher,
        AdapterView.OnItemSelectedListener, OnCheckedChangeListener {
    private final WifiDialog mWifiDialog;
    private final View mView;
    private final AccessPoint mAccessPoint;

    private boolean mEdit;

    private TextView mSsidView;

    private int mAccessPointSecurity;
    private TextView mPasswordView;

    private Spinner mSecuritySpinner;
    // Allows underscore char to supports proxies that do not
    // follow the spec
    private static final String HC = "a-zA-Z0-9\\_";

    // Matches blank input, ips, and domain names
    private static final String HOSTNAME_REGEXP = "^$|^[" + HC + "]+(\\-[" + HC
            + "]+)*(\\.[" + HC + "]+(\\-[" + HC + "]+)*)*$";
    private static final Pattern HOSTNAME_PATTERN;
    private static final String EXCLUSION_REGEXP = "$|^(\\*)?\\.?[" + HC
            + "]+(\\-[" + HC + "]+)*(\\.[" + HC + "]+(\\-[" + HC + "]+)*)*$";
    private static final Pattern EXCLUSION_PATTERN;
    static {
        HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
        EXCLUSION_PATTERN = Pattern.compile(EXCLUSION_REGEXP);
    }

    /* This value comes from "wifi_ip_settings" resource array */
    private static final int DHCP = 0;
    private static final int STATIC_IP = 1;

    /* These values come from "wifi_proxy_settings" resource array */
    public static final int PROXY_NONE = 0;
    public static final int PROXY_STATIC = 1;

    private static final String TAG = "WifiConfigController";

    private Spinner mIpSettingsSpinner;
    private TextView mIpAddressView;
    private TextView mGatewayView;
    private TextView mNetworkPrefixLengthView;
    private TextView mDns1View;
    private TextView mDns2View;

    private Spinner mProxySettingsSpinner;
    private TextView mProxyHostView;
    private TextView mProxyPortView;
    private TextView mProxyExclusionListView;

    private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
    private ProxySettings mProxySettings = ProxySettings.UNASSIGNED;
    private LinkProperties mLinkProperties = new LinkProperties();

    private final Handler mTextViewChangedHandler;

    public WifiConfigController(WifiDialog dialog, View view,
            AccessPoint accessPoint, boolean edit) {
        mWifiDialog = dialog;
        mView = view;
        mAccessPoint = accessPoint;
        mAccessPointSecurity = (accessPoint == null) ? AccessPoint.SECURITY_NONE
                : accessPoint.security;
        mEdit = edit;

        mTextViewChangedHandler = new Handler();
        final Context context = mWifiDialog.getContext();
        final Resources resources = context.getResources();

        mIpSettingsSpinner = (Spinner) mView.findViewById(R.id.ip_settings);
        mIpSettingsSpinner.setOnItemSelectedListener(this);
        mProxySettingsSpinner = (Spinner) mView
                .findViewById(R.id.proxy_settings);
        mProxySettingsSpinner.setOnItemSelectedListener(this);

        if (mAccessPoint == null) { // new network
            mWifiDialog.setTitle(R.string.wifi_add_network);

            mSsidView = (TextView) mView.findViewById(R.id.ssid);
            mSsidView.addTextChangedListener(this);
            mSecuritySpinner = ((Spinner) mView.findViewById(R.id.security));
            mSecuritySpinner.setOnItemSelectedListener(this);

            mView.findViewById(R.id.type).setVisibility(View.VISIBLE);

            showIpConfigFields();
            showProxyFields();
            mView.findViewById(R.id.wifi_advanced_toggle).setVisibility(
                    View.VISIBLE);
            ((CheckBox) mView.findViewById(R.id.wifi_advanced_togglebox))
                    .setOnCheckedChangeListener(this);

            mWifiDialog.setSubmitButton(context.getString(R.string.wifi_save));
        } else {
            mWifiDialog.setTitle(mAccessPoint.ssid);

            ViewGroup group = (ViewGroup) mView.findViewById(R.id.info);

            DetailedState state = mAccessPoint.getState();
            if (state != null) {
                String summary = null;
                String[] formats = context.getResources().getStringArray(
                        R.array.wifi_status);
                int index = state.ordinal();
                if (index >= formats.length || formats[index].length() == 0) {
                    addRow(group, R.string.wifi_status, null);
                } else {
                    addRow(group, R.string.wifi_status, formats[index]);
                }
            }

            int level = mAccessPoint.getLevel();
            if (level != -1) {
                String[] signal = resources.getStringArray(R.array.wifi_signal);
                addRow(group, R.string.wifi_signal, signal[level]);
            }

            WifiInfo info = mAccessPoint.getInfo();
            if (info != null && info.getLinkSpeed() != -1) {
                addRow(group, R.string.wifi_speed, info.getLinkSpeed()
                        + WifiInfo.LINK_SPEED_UNITS);
            }

            addRow(group, R.string.wifi_security,
                    mAccessPoint.getSecurityString(false));

            boolean showAdvancedFields = false;
            if (mAccessPoint.networkId != INVALID_NETWORK_ID) {
                WifiConfiguration config = mAccessPoint.getConfig();
                if (config.ipAssignment == IpAssignment.STATIC) {
                    mIpSettingsSpinner.setSelection(STATIC_IP);
                    showAdvancedFields = true;
                } else {
                    mIpSettingsSpinner.setSelection(DHCP);
                }
                // Display IP addresses
                for (InetAddress a : config.linkProperties.getAddresses()) {
                    addRow(group, R.string.wifi_ip_address, a.getHostAddress());
                }

                if (config.proxySettings == ProxySettings.STATIC) {
                    mProxySettingsSpinner.setSelection(PROXY_STATIC);
                    showAdvancedFields = true;
                } else if (config.proxySettings == ProxySettings.PAC) {
                    mProxySettingsSpinner.setVisibility(View.GONE);
                    TextView textView = (TextView) mView
                            .findViewById(R.id.proxy_pac_info);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(context.getString(R.string.proxy_url)
                            + config.linkProperties.getHttpProxy()
                                    .getPacFileUrl());
                    showAdvancedFields = true;
                } else {
                    mProxySettingsSpinner.setSelection(PROXY_NONE);
                }
            }

            if (mAccessPoint.networkId == INVALID_NETWORK_ID || mEdit) {
                showSecurityFields();
                showIpConfigFields();
                showProxyFields();
                mView.findViewById(R.id.wifi_advanced_toggle).setVisibility(
                        View.VISIBLE);
                ((CheckBox) mView.findViewById(R.id.wifi_advanced_togglebox))
                        .setOnCheckedChangeListener(this);
                if (showAdvancedFields) {
                    ((CheckBox) mView
                            .findViewById(R.id.wifi_advanced_togglebox))
                            .setChecked(true);
                    mView.findViewById(R.id.wifi_advanced_fields)
                            .setVisibility(View.VISIBLE);
                }
            }

            if (mEdit) {
                mWifiDialog.setSubmitButton(context
                        .getString(R.string.wifi_save));
            } else {
                if (state == null && level != -1) {
                    mWifiDialog.setSubmitButton(context
                            .getString(R.string.wifi_connect));
                } else {
                    mView.findViewById(R.id.ip_fields).setVisibility(View.GONE);
                }
                if (mAccessPoint.networkId != INVALID_NETWORK_ID) {
                    mWifiDialog.setForgetButton(context
                            .getString(R.string.wifi_forget));
                }
            }
        }

        mWifiDialog.setCancelButton(context.getString(R.string.wifi_cancel));
        if (mWifiDialog.getSubmitButton() != null) {
            enableSubmitIfAppropriate();
        }
    }

    private void addRow(ViewGroup group, int name, String value) {
        View row = mWifiDialog.getLayoutInflater().inflate(
                R.layout.wifi_dialog_row, group, false);
        ((TextView) row.findViewById(R.id.name)).setText(name);
        ((TextView) row.findViewById(R.id.value)).setText(value);
        group.addView(row);
    }

    /* show submit button if password, ip and proxy settings are valid */
    void enableSubmitIfAppropriate() {
        Button submit = mWifiDialog.getSubmitButton();
        if (submit == null)
            return;

        boolean enabled = false;
        boolean passwordInvalid = false;

        if (mPasswordView != null
                && ((mAccessPointSecurity == AccessPoint.SECURITY_WEP && mPasswordView
                        .length() == 0) || (mAccessPointSecurity == AccessPoint.SECURITY_PSK && mPasswordView
                        .length() < 8))) {
            passwordInvalid = true;
        }

        if ((mSsidView != null && mSsidView.length() == 0)
                || ((mAccessPoint == null || mAccessPoint.networkId == INVALID_NETWORK_ID) && passwordInvalid)) {
            enabled = false;
        } else {
            if (ipAndProxyFieldsAreValid()) {
                enabled = true;
            } else {
                enabled = false;
            }
        }
        submit.setEnabled(enabled);
    }

    /* package */WifiConfiguration getConfig() {
        if (mAccessPoint != null
                && mAccessPoint.networkId != INVALID_NETWORK_ID && !mEdit) {
            return null;
        }

        WifiConfiguration config = new WifiConfiguration();

        if (mAccessPoint == null) {
            config.SSID = AccessPoint.convertToQuotedString(mSsidView.getText()
                    .toString());
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else if (mAccessPoint.networkId == INVALID_NETWORK_ID) {
            config.SSID = AccessPoint.convertToQuotedString(mAccessPoint.ssid);
        } else {
            config.networkId = mAccessPoint.networkId;
        }

        switch (mAccessPointSecurity) {
        case AccessPoint.SECURITY_NONE:
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            break;

        case AccessPoint.SECURITY_WEP:
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
            if (mPasswordView.length() != 0) {
                int length = mPasswordView.length();
                String password = mPasswordView.getText().toString();
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((length == 10 || length == 26 || length == 58)
                        && password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
            }
            break;

        case AccessPoint.SECURITY_PSK:
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            if (mPasswordView.length() != 0) {
                String password = mPasswordView.getText().toString();
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = '"' + password + '"';
                }
            }
            break;
        default:
            return null;
        }
        config.proxySettings = mProxySettings;
        config.ipAssignment = mIpAssignment;
        config.linkProperties = new LinkProperties(mLinkProperties);

        return config;
    }

    private boolean ipAndProxyFieldsAreValid() {
        mLinkProperties.clear();
        mIpAssignment = (mIpSettingsSpinner != null && mIpSettingsSpinner
                .getSelectedItemPosition() == STATIC_IP) ? IpAssignment.STATIC
                : IpAssignment.DHCP;

        if (mIpAssignment == IpAssignment.STATIC) {
            int result = validateIpConfigFields(mLinkProperties);
            if (result != 0) {
                return false;
            }
        }

        mProxySettings = (mProxySettingsSpinner != null && mProxySettingsSpinner
                .getSelectedItemPosition() == PROXY_STATIC) ? ProxySettings.STATIC
                : ProxySettings.NONE;

        if (mProxySettings == ProxySettings.STATIC && mProxyHostView != null) {
            String host = mProxyHostView.getText().toString();
            String portStr = mProxyPortView.getText().toString();
            String exclusionList = mProxyExclusionListView.getText().toString();
            int port = 0;
            int result = 0;
            try {
                port = Integer.parseInt(portStr);
                result = validateProxyConfigFields(host, portStr, exclusionList);
            } catch (NumberFormatException e) {
                result = R.string.proxy_error_invalid_port;
            }
            if (result == 0) {
                ProxyProperties proxyProperties = new ProxyProperties(host,
                        port, exclusionList);
                mLinkProperties.setHttpProxy(proxyProperties);
            } else {
                return false;
            }
        }
        return true;
    }

    private int validateIpConfigFields(LinkProperties linkProperties) {
        if (mIpAddressView == null)
            return 0;

        String ipAddr = mIpAddressView.getText().toString();
        if (TextUtils.isEmpty(ipAddr))
            return R.string.wifi_ip_settings_invalid_ip_address;

        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
        } catch (IllegalArgumentException e) {
            return R.string.wifi_ip_settings_invalid_ip_address;
        }

        int networkPrefixLength = -1;
        try {
            networkPrefixLength = Integer.parseInt(mNetworkPrefixLengthView
                    .getText().toString());
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                return R.string.wifi_ip_settings_invalid_network_prefix_length;
            }
            linkProperties.addLinkAddress(new LinkAddress(inetAddr,
                    networkPrefixLength));
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
            mNetworkPrefixLengthView.setText(mWifiDialog.getContext()
                    .getString(R.string.wifi_network_prefix_length_hint));
        }

        String gateway = mGatewayView.getText().toString();
        if (TextUtils.isEmpty(gateway)) {
            try {
                // Extract a default gateway from IP address
                InetAddress netPart = NetworkUtils.getNetworkPart(inetAddr,
                        networkPrefixLength);
                byte[] addr = netPart.getAddress();
                addr[addr.length - 1] = 1;
                mGatewayView.setText(InetAddress.getByAddress(addr)
                        .getHostAddress());
            } catch (RuntimeException ee) {
            } catch (java.net.UnknownHostException u) {
            }
        } else {
            InetAddress gatewayAddr = null;
            try {
                gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
            } catch (IllegalArgumentException e) {
                return R.string.wifi_ip_settings_invalid_gateway;
            }
            linkProperties.addRoute(new RouteInfo(gatewayAddr));
        }

        String dns = mDns1View.getText().toString();
        InetAddress dnsAddr = null;

        if (TextUtils.isEmpty(dns)) {
            // If everything else is valid, provide hint as a default option
            mDns1View.setText(mWifiDialog.getContext().getString(
                    R.string.wifi_dns1_hint));
        } else {
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns);
            } catch (IllegalArgumentException e) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            linkProperties.addDns(dnsAddr);
        }

        if (mDns2View.length() > 0) {
            dns = mDns2View.getText().toString();
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns);
            } catch (IllegalArgumentException e) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            linkProperties.addDns(dnsAddr);
        }
        return 0;
    }

    private int validateProxyConfigFields(String hostname, String port,
            String exclList) {

        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        String exclListArray[] = exclList.split(",");

        if (!match.matches())
            return R.string.proxy_error_invalid_host;

        for (String excl : exclListArray) {
            Matcher m = EXCLUSION_PATTERN.matcher(excl);
            if (!m.matches())
                return R.string.proxy_error_invalid_exclusion_list;
        }

        if (hostname.length() > 0 && port.length() == 0) {
            return R.string.proxy_error_empty_port;
        }

        if (port.length() > 0) {
            if (hostname.length() == 0) {
                return R.string.proxy_error_empty_host_set_port;
            }
            int portVal = -1;
            try {
                portVal = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                return R.string.proxy_error_invalid_port;
            }
            if (portVal <= 0 || portVal > 0xFFFF) {
                return R.string.proxy_error_invalid_port;
            }
        }
        return 0;
    }

    private void showSecurityFields() {
        if (mAccessPointSecurity == AccessPoint.SECURITY_NONE) {
            mView.findViewById(R.id.security_fields).setVisibility(View.GONE);
            return;
        }
        mView.findViewById(R.id.security_fields).setVisibility(View.VISIBLE);

        if (mPasswordView == null) {
            mPasswordView = (TextView) mView.findViewById(R.id.password);
            mPasswordView.addTextChangedListener(this);
            ((CheckBox) mView.findViewById(R.id.show_password))
                    .setOnCheckedChangeListener(this);

            if (mAccessPoint != null
                    && mAccessPoint.networkId != INVALID_NETWORK_ID) {
                mPasswordView.setHint(R.string.wifi_unchanged);
            }
        }

    }

    private void setPasswordInvisible() {
        mPasswordView.setText("");
        mView.findViewById(R.id.password_layout).setVisibility(View.GONE);
        mView.findViewById(R.id.show_password_layout).setVisibility(View.GONE);
    }

    private void showIpConfigFields() {
        WifiConfiguration config = null;

        mView.findViewById(R.id.ip_fields).setVisibility(View.VISIBLE);

        if (mAccessPoint != null
                && mAccessPoint.networkId != INVALID_NETWORK_ID) {
            config = mAccessPoint.getConfig();
        }

        if (mIpSettingsSpinner.getSelectedItemPosition() == STATIC_IP) {
            mView.findViewById(R.id.staticip).setVisibility(View.VISIBLE);
            if (mIpAddressView == null) {
                mIpAddressView = (TextView) mView.findViewById(R.id.ipaddress);
                mIpAddressView.addTextChangedListener(this);
                mGatewayView = (TextView) mView.findViewById(R.id.gateway);
                mGatewayView.addTextChangedListener(this);
                mNetworkPrefixLengthView = (TextView) mView
                        .findViewById(R.id.network_prefix_length);
                mNetworkPrefixLengthView.addTextChangedListener(this);
                mDns1View = (TextView) mView.findViewById(R.id.dns1);
                mDns1View.addTextChangedListener(this);
                mDns2View = (TextView) mView.findViewById(R.id.dns2);
                mDns2View.addTextChangedListener(this);
            }
            if (config != null) {
                LinkProperties linkProperties = config.linkProperties;
                Iterator<LinkAddress> iterator = linkProperties
                        .getLinkAddresses().iterator();
                if (iterator.hasNext()) {
                    LinkAddress linkAddress = iterator.next();
                    mIpAddressView.setText(linkAddress.getAddress()
                            .getHostAddress());
                    mNetworkPrefixLengthView.setText(Integer
                            .toString(linkAddress.getNetworkPrefixLength()));
                }

                for (RouteInfo route : linkProperties.getRoutes()) {
                    if (route.isDefaultRoute()) {
                        mGatewayView.setText(route.getGateway()
                                .getHostAddress());
                        break;
                    }
                }

                Iterator<InetAddress> dnsIterator = linkProperties.getDnses()
                        .iterator();
                if (dnsIterator.hasNext()) {
                    mDns1View.setText(dnsIterator.next().getHostAddress());
                }
                if (dnsIterator.hasNext()) {
                    mDns2View.setText(dnsIterator.next().getHostAddress());
                }
            }
        } else {
            mView.findViewById(R.id.staticip).setVisibility(View.GONE);
        }
    }

    private void showProxyFields() {
        WifiConfiguration config = null;

        mView.findViewById(R.id.proxy_settings_fields).setVisibility(
                View.VISIBLE);

        if (mAccessPoint != null
                && mAccessPoint.networkId != INVALID_NETWORK_ID) {
            config = mAccessPoint.getConfig();
        }

        if (mProxySettingsSpinner.getSelectedItemPosition() == PROXY_STATIC) {
            mView.findViewById(R.id.proxy_warning_limited_support)
                    .setVisibility(View.VISIBLE);
            mView.findViewById(R.id.proxy_fields).setVisibility(View.VISIBLE);
            if (mProxyHostView == null) {
                mProxyHostView = (TextView) mView
                        .findViewById(R.id.proxy_hostname);
                mProxyHostView.addTextChangedListener(this);
                mProxyPortView = (TextView) mView.findViewById(R.id.proxy_port);
                mProxyPortView.addTextChangedListener(this);
                mProxyExclusionListView = (TextView) mView
                        .findViewById(R.id.proxy_exclusionlist);
                mProxyExclusionListView.addTextChangedListener(this);
            }
            if (config != null) {
                ProxyProperties proxyProperties = config.linkProperties
                        .getHttpProxy();
                if (proxyProperties != null) {
                    mProxyHostView.setText(proxyProperties.getHost());
                    mProxyPortView.setText(Integer.toString(proxyProperties
                            .getPort()));
                    mProxyExclusionListView.setText(proxyProperties
                            .getExclusionList());
                }
            }
        } else {
            mView.findViewById(R.id.proxy_warning_limited_support)
                    .setVisibility(View.GONE);
            mView.findViewById(R.id.proxy_fields).setVisibility(View.GONE);
        }
    }

    private void setSelection(Spinner spinner, String value) {
        if (value != null) {
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner
                    .getAdapter();
            for (int i = adapter.getCount() - 1; i >= 0; --i) {
                if (value.equals(adapter.getItem(i))) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    public boolean isEdit() {
        return mEdit;
    }

    @Override
    public void afterTextChanged(Editable s) {
        mTextViewChangedHandler.post(new Runnable() {
            public void run() {
                enableSubmitIfAppropriate();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        // work done in afterTextChanged
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // work done in afterTextChanged
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view.getId() == R.id.show_password) {
            int pos = mPasswordView.getSelectionEnd();
            mPasswordView
                    .setInputType(InputType.TYPE_CLASS_TEXT
                            | (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                    : InputType.TYPE_TEXT_VARIATION_PASSWORD));
            if (pos >= 0) {
                ((EditText) mPasswordView).setSelection(pos);
            }
        } else if (view.getId() == R.id.wifi_advanced_togglebox) {
            if (isChecked) {
                mView.findViewById(R.id.wifi_advanced_fields).setVisibility(
                        View.VISIBLE);
            } else {
                mView.findViewById(R.id.wifi_advanced_fields).setVisibility(
                        View.GONE);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (parent == mSecuritySpinner) {
            mAccessPointSecurity = position;
            showSecurityFields();
        } else if (parent == mProxySettingsSpinner) {
            showProxyFields();
        } else {
            showIpConfigFields();
        }
        enableSubmitIfAppropriate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //
    }

    /**
     * Make the characters of the password visible if show_password is checked.
     */
    private void updatePasswordVisibility(boolean checked) {
        int pos = mPasswordView.getSelectionEnd();
        mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT
                | (checked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        : InputType.TYPE_TEXT_VARIATION_PASSWORD));
        if (pos >= 0) {
            ((EditText) mPasswordView).setSelection(pos);
        }
    }
}
