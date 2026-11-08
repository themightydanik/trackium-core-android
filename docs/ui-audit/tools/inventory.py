#!/usr/bin/env python3
"""Read-only exhaustive UI resource/class inventory. Output paths are repository-relative."""
from pathlib import Path
import json,re,xml.etree.ElementTree as E
R=Path(__file__).resolve().parents[3]; N='{http://schemas.android.com/apk/res/android}'
roots=['app/src/main','app/src/block','app/src/pandaShell','integrated/history/src/main','integrated/trackium/src/main']
result={'scope':roots,'activities':[],'screen_classes':[],'layouts':[],'values':[],'graphics':[],'brand_references':[],'debug_harnesses':[]}
for base in roots:
 for p in sorted((R/base).rglob('*')):
  if not p.is_file():continue
  name=str(p.relative_to(R))
  if p.name=='AndroidManifest.xml':
   for a in E.parse(p).getroot().findall('.//activity'):
    result['activities'].append({'manifest':name,'name':a.get(N+'name'),'exported':a.get(N+'exported','false (no intent filter)'),'theme':a.get(N+'theme','inherited')})
  if p.suffix in ['.java','.kt']:
   s=p.read_text()
   if re.search(r'class\s+\w+\s*(?:extends|:)\s*(?:\w*Activity|\w*Fragment|BaseView)',s):
    result['screen_classes'].append({'file':name,'layouts':sorted(set(re.findall(r'R.layout.(\w+)',s))),'dialogs':len(re.findall(r'AlertDialog|LanguageDialog|\.setTitle\(',s))})
  if 'res' in p.parts:
   folder=p.parent.name
   if folder.startswith('layout'):result['layouts'].append(name)
   if folder.startswith('values') and p.suffix=='.xml':
    for x in E.parse(p).getroot():
     if x.tag in ['style','color','dimen']:
      result['values'].append({'file':name,'kind':x.tag,'name':x.get('name'),'parent':x.get('parent'),'value':x.text.strip() if x.text and x.text.strip() else None})
   if folder.startswith(('drawable','mipmap','font')):result['graphics'].append(name)
  if p.suffix in ['.java','.kt','.xml']:
   for i,line in enumerate(p.read_text().splitlines(),1):
    if re.search(r'(?:R\.(?:drawable|mipmap)\.|@(?:drawable|mipmap)/)(?:ic_minima\w*|ic_launcher\w*)|launcher_pupil',line):
     result['brand_references'].append({'file':name,'line':i,'reference':line.strip()[:240]})
for p in sorted((R/'app/src/debug').rglob('*.java')):
 result['debug_harnesses'].append(str(p.relative_to(R)))
out=R/'docs/ui-audit/inventory.json';out.write_text(json.dumps(result,indent=2,ensure_ascii=False)+'\n')
print({k:len(v) for k,v in result.items() if k!='scope'})
