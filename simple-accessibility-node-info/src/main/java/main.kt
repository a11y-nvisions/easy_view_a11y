package com.example.simple_accessibility_node_info
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Switch
import android.widget.ToggleButton
import com.google.android.material.R as MaterialRes
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat
import com.google.android.material.tabs.TabLayout.Tab

enum class AccessibilityRole(val value: CharSequence) {
    BUTTON(Button::class.java.name),
    SWITCH(Switch::class.java.name),
    TAB(Tab::class.java.name),
    CHECKBOX(CheckBox::class.java.name),
    RADIO_BUTTON(RadioButton::class.java.name),
    TOGGLE_BUTTON(ToggleButton::class.java.name)
}

data class EasyA11yNodeInfoInit (
    var checkable:Boolean? = null,
    var checked:Boolean? = null,
    var itemCount:Int? = null,
    var itemIndex:Int? = null,
    var heading:Boolean? = null,
    var selected:Boolean? = null,
    var expanded:Boolean? = null,
    var label:String? = null,
    var role: AccessibilityRole? = null,
    var hint:String? = null,
)

class EasyA11yNodeInfoManager {
    private val semantics: EasyA11yNodeInfoInit;
    val view:View;

    constructor( view: View, init: EasyA11yNodeInfoInit? = null ) {
        this.view = view;
        this.semantics = init?.let { it }?: EasyA11yNodeInfoInit();

        this.view.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfo
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val infoCompat = AccessibilityNodeInfoCompat.wrap(info)

                isCheckable?.let {
                    info.isCheckable =  it;
                }
                isChecked?.let {
                    info.isChecked =  it;
                }
                isSelected?.let {
                    info.isSelected =  it;
                }
                isHeading?.let {
                    info.isHeading =  it;
                }
                isExpanded?.let {
                    if(it) {
                        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
                    } else {
                        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
                    }
                }
                label?.let {
                    info.contentDescription = it
                }
                hint?.let {
                    info.hintText = it
                }
                role?.let {
                    info.className = it.value
                    if(it.value == AccessibilityRole.TAB.value) {
                        infoCompat.roleDescription = view.resources.getString(MaterialRes.string.item_view_role_description)
                        infoCompat.isCheckable = false
                        infoCompat.isChecked = false

                        val index = itemIndex
                        val count = itemCount
                        val selected = isSelected
                        if(index != null && count != null && selected != null) {
                            infoCompat.setCollectionInfo(CollectionInfoCompat.obtain(1, count,false, CollectionInfo.SELECTION_MODE_SINGLE))
                            infoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(0,1,index,1,false, selected))
                            if(selected) {
                                infoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                                infoCompat.isClickable = false;
                            }
                        }
                    }
                }
            }
        }

        this.isChecked = this.semantics.checked;
        this.isCheckable = this.semantics.checkable;
        this.hint = this.semantics.hint;
        this.label = this.semantics.label;
        this.role = this.semantics.role;
        this.isExpanded = this.semantics.expanded;
        this.isSelected = this.semantics.selected;
        reloadNodeInfo();
    }

    fun setItemIndex(num: Int): EasyA11yNodeInfoManager {
        this.itemIndex = num;
        this.reloadNodeInfo();
        return this;
    }
    private var itemIndex:Int?
        get() = this.semantics.itemIndex
        set(num) { this.semantics.itemIndex = num; }

    fun setItemCount(num: Int): EasyA11yNodeInfoManager {
        this.itemCount = num;
        this.reloadNodeInfo();
        return this;
    }
    private var itemCount:Int?
        get() = this.semantics.itemCount
        set(num) { this.semantics.itemCount = num; }

    fun setHeading(boolean: Boolean): EasyA11yNodeInfoManager {
        this.isHeading = boolean;
        this.reloadNodeInfo();
        return this;
    }
    private var isHeading:Boolean?
        get() = this.semantics.heading
        set(v) { this.semantics.heading = v; }

    fun setSelected(boolean: Boolean): EasyA11yNodeInfoManager {
        this.isSelected = boolean;
        this.reloadNodeInfo();
        return this;
    }
    private var isSelected:Boolean?
        get() = this.semantics.selected
        set(v) { this.semantics.selected = v; }

    fun setCheckable(boolean:Boolean): EasyA11yNodeInfoManager {
        this.isCheckable = boolean;
        this.reloadNodeInfo();
        return this;
    }
    private var isCheckable:Boolean?
        get() = this.semantics.checkable;
        set(v) {  this.semantics.checkable = v; }

    fun setChecked(boolean:Boolean): EasyA11yNodeInfoManager {
        this.isChecked = boolean;
        this.reloadNodeInfo();
        return this;
    }
    private var isChecked:Boolean?
        get() = this.semantics.checked;
        set(v) {  this.semantics.checked = v;}

    fun setExpanded(boolean:Boolean): EasyA11yNodeInfoManager {
        this.isExpanded = boolean;
        this.reloadNodeInfo();
        return this;
    }
    private var isExpanded:Boolean?
        get() = this.semantics.expanded;
        set(v) {  this.semantics.expanded = v;}

    fun setLabel(string:String): EasyA11yNodeInfoManager {
        this.label = string;
        this.reloadNodeInfo();
        return this;
    }
    private var label:String?
        get() = this.semantics.label;
        set(v) {  this.semantics.label = v;}
    fun setHint(string:String): EasyA11yNodeInfoManager {
        this.hint = string;
        this.reloadNodeInfo();
        return this;
    }
    private var hint:String?
        get() = this.semantics.hint;
        set(v) {  this.semantics.hint = v;}

    fun setRole(role: AccessibilityRole): EasyA11yNodeInfoManager {
        this.role = role;
        this.reloadNodeInfo();
        return this;
    }
    private var role: AccessibilityRole?
        get() = this.semantics.role
        set(v) {  this.semantics.role = v;}

    private fun reloadNodeInfo() {
        this.view.createAccessibilityNodeInfo();
    }
}

val View.isNodeInfoInitialized:Boolean
    get() { return this.getTag(R.id.easy_a11y_node_info) == null }

val View.nodeInfo: EasyA11yNodeInfoManager
    get() {
        return if( !isNodeInfoInitialized ) {
            this.getTag(R.id.easy_a11y_node_info) as EasyA11yNodeInfoManager // return
        } else {
            val manager = EasyA11yNodeInfoManager(this)
            this.setTag(R.id.easy_a11y_node_info, manager)
            manager // return
        }
    }