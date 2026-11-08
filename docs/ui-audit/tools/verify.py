#!/usr/bin/env python3
"""Check evidence hashes, contrast calculations and non-shipping proposal syntax."""
from pathlib import Path
import hashlib,json,re,xml.etree.ElementTree as E
ROOT=Path(__file__).resolve().parents[1]
evidence=ROOT/'evidence';proposal=ROOT/'proposals'
for name,sha in json.loads((evidence/'screenshots-sha256.json').read_text()).items():
 p=evidence/name
 assert p.parent==evidence and p.suffix=='.png'
 assert hashlib.sha256(p.read_bytes()).hexdigest()==sha,name
 assert p.read_bytes()[:8]==b'\x89PNG\r\n\x1a\n',name
captures=json.loads((evidence/'web-capture-metadata.json').read_text())['records']
assert len(captures)==28
assert all((evidence/r['path']).exists() and not r['errors'] and r['horizontalOverflow']==0 for r in captures)
for path in proposal.glob('*.svg'):E.parse(path)
for x in E.parse(proposal/'colors-draft.xml').getroot():assert re.fullmatch('[a-z][a-z0-9_]*',x.get('name'))
def luma(value):
 rgb=[int(value[i:i+2],16)/255 for i in [1,3,5]]
 return sum(c*w for c,w in zip([x/12.92 if x<=.04045 else ((x+.055)/1.055)**2.4 for x in rgb],[.2126,.7152,.0722]))
tokens=json.loads((proposal/'tokens.json').read_text())
for c in json.loads((proposal/'contrast-checks.json').read_text()):
 if c['scheme']=='existing host':continue
 scheme=tokens['schemes'][c['scheme']];low,high=sorted([luma(scheme[c['foreground']]),luma(scheme[c['background']])]);ratio=round((high+.05)/(low+.05),2)
 assert ratio==c['ratio'] and ratio>=c['threshold'],c
print('Evidence PNG hashes, 28 browser records, draft SVG/XML syntax and functional contrast: PASS')
