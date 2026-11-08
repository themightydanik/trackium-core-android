#!/usr/bin/env python3
"""Generate Android and CSS mappings from the canonical DS1 source; no runtime data."""
import argparse,json,re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
def snake(k):return re.sub('[A-Z]',lambda m:'_'+m[0].lower(),k)
def outputs(root=ROOT):
 j=json.loads((root/'design-system/tokens.json').read_text());res=root/'design-system/src/main/res';out={}
 for scheme,qual in [('light','values'),('dark','values-night')]:
  out[res/qual/'tc_ds1_colors.xml']='<resources>\n'+''.join(f'    <color name="tc_ds1_{snake(k)}">{v}</color>\n' for k,v in j['schemes'][scheme].items())+'</resources>\n'
 dims={**{'type_'+k:(v[0],'sp') for k,v in j['typeSpRem'].items()},**{'space_'+str(v):(v,'dp') for v in j['spaceDpPx']},**{'radius_'+k:(v,'dp') for k,v in j['radiusDpPx'].items()},**{'icon_'+k:(v,'dp') for k,v in j['iconDpPx'].items()},**{'elevation_'+k:(v,'dp') for k,v in j['elevationDp'].items()},'touch_target':(48,'dp')}
 out[res/'values/tc_ds1_dimensions.xml']='<resources>\n'+''.join(f'    <dimen name="tc_ds1_{k}">{v}{unit}</dimen>\n' for k,(v,unit) in dims.items())+'</resources>\n'
 styles='<resources>\n'
 for k,v in j['typeSpRem'].items():styles+=f'    <style name="TextAppearance.TrackiumDS1.{k.title()}" parent="android:style/TextAppearance.Material.Body1"><item name="android:textSize">@dimen/tc_ds1_type_{k}</item><item name="android:textColor">@color/tc_ds1_text</item><item name="android:fontFamily">sans</item><item name="android:lineHeight">{j["typeLineHeightSp"][k]}sp</item><item name="android:textFontWeight">{j["typeWeight"][k]}</item></style>\n'
 out[res/'values/tc_ds1_type.xml']=styles+'</resources>\n'
 for kind,role in [('primary_content','on_accent'),('primary_container','accent'),('secondary_content','accent')]:
  disabled='disabled_container' if kind.endswith('container') else 'disabled_text'
  out[res/'color'/f'tc_ds1_{kind}.xml']=f'<selector xmlns:android="http://schemas.android.com/apk/res/android"><item android:state_enabled="false" android:color="@color/tc_ds1_{disabled}"/><item android:color="@color/tc_ds1_{role}"/></selector>\n'
 roles=list(j['schemes']['light']);enum=',\n        '.join(f'{snake(k).upper()}(R.color.tc_ds1_{snake(k)})' for k in roles)
 out[root/'design-system/src/main/java/tech/trackium/designsystem/TrackiumTokens.kt']='''package tech.trackium.designsystem
import android.content.Context
/** Generated DS1 semantic roles. Resource qualifiers follow the supplied Context's UI mode. */
object TrackiumTokens {
    const val VERSION = "1.0.0"
    enum class ColorRole(val resource: Int) {
        '''+enum+'''
    }
    @JvmStatic fun color(context: Context, role: ColorRole): Int = context.resources.getColor(role.resource, context.theme)
    const val TOUCH_TARGET_DP = 48
}
'''
 def css(scheme):return ''.join(f'  --tc-{snake(k).replace("_","-")}: {v};\n' for k,v in j['schemes'][scheme].items())
 # Consumers explicitly use tc roles; legacy v2 aliases are migrated in the later web UI stage.
 text='/* GENERATED DS1 1.0.0 — canonical source: trackium-core-android/design-system/tokens.json */\n:root {\n'+css('light')
 for k,v in j['typeSpRem'].items():
  text+=f'  --tc-type-{k}: {v[1]}rem;\n  --tc-leading-{k}: {j["typeLineHeightSp"][k]/v[0]:.4f};\n  --tc-weight-{k}: {j["typeWeight"][k]};\n'
 for v in j['spaceDpPx']:text+=f'  --tc-space-{v}: {v}px;\n'
 for k,v in j['radiusDpPx'].items():text+=f'  --tc-radius-{k}: {v}px;\n'
 for k,v in j['elevationDp'].items():text+=f'  --tc-elevation-{k}: {v};\n'
 for k,v in j['iconDpPx'].items():text+=f'  --tc-icon-{k}: {v}px;\n'
 for k,v in j['motionMs'].items():text+=f'  --tc-motion-{k}: {v}ms;\n'
 text+='  --tc-button-primary-bg: var(--tc-accent);\n  --tc-button-primary-text: var(--tc-on-accent);\n  --tc-button-secondary-text: var(--tc-accent);\n  --tc-control-disabled-bg: var(--tc-disabled-container);\n  --tc-control-disabled-text: var(--tc-disabled-text);\n  --tc-control-focus: var(--tc-focus);\n  --tc-control-error: var(--tc-danger);\n  --tc-touch-target: 44px;\n}\n@media (prefers-color-scheme: dark) { :root:not([data-tc-theme="light"]) {\n'+css('dark')+'} }\n:root[data-tc-theme="dark"] {\n'+css('dark')+'}\n:root[data-tc-theme="light"] {\n'+css('light')+'}\n@media (prefers-reduced-motion: reduce) { :root { --tc-motion-feedback: 0ms; --tc-motion-transition: 0ms; --tc-motion-sheet: 0ms; } }\n'
 out[root/'design-system/generated/tokens.css']=text
 return out
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--check',action='store_true');args=p.parse_args()
 for path,text in outputs().items():
  if args.check:
   if not path.exists() or path.read_text()!=text:raise SystemExit('Stale token mapping: '+str(path))
  else:path.parent.mkdir(parents=True,exist_ok=True);path.write_text(text)
 print('Token mappings verified' if args.check else 'Token mappings generated')
