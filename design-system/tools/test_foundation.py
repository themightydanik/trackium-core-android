#!/usr/bin/env python3
import unittest,json,hashlib,subprocess,sys,math
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[2]
class FoundationTest(unittest.TestCase):
 def test_generated_mappings_not_stale(self):
  subprocess.run([sys.executable,str(ROOT/'design-system/tools/generate_tokens.py'),'--check'],check=True,capture_output=True)
 def test_semantic_roles_match_and_contrast(self):
  j=json.loads((ROOT/'design-system/tokens.json').read_text());self.assertEqual(set(j['schemes']['dark']),set(j['schemes']['light']))
  def lum(h):
   c=[int(h[i:i+2],16)/255 for i in [1,3,5]];return sum(x*w for x,w in zip([v/12.92 if v<=.04045 else ((v+.055)/1.055)**2.4 for v in c],[.2126,.7152,.0722]))
  for scheme in j['schemes'].values():
   for bg in ['canvas','surface','surfaceRaised']:
    for fg in ['text','textSecondary','accent','warning','danger','gold','outline']:
     a,b=sorted([lum(scheme[fg]),lum(scheme[bg])]);self.assertGreaterEqual((b+.05)/(a+.05),3 if fg=='outline' else 4.5,(fg,bg))
   a,b=sorted([lum(scheme['accent']),lum(scheme['onAccent'])]);self.assertGreaterEqual((b+.05)/(a+.05),4.5)
 def test_source_crop_is_exact_rgba(self):
  b=ROOT/'design-system/brand';j=json.loads((b/'manifest.json').read_text());source=Image.open(b/'source/owner-wordmark.png').convert('RGBA');crop=Image.open(b/'generated/mark.png').convert('RGBA');self.assertEqual(crop.tobytes(),source.crop(j['crop_bbox']).tobytes())
  self.assertEqual(hashlib.sha256((b/'source/owner-wordmark.png').read_bytes()).hexdigest(),j['wordmark_sha256'])
 def test_mask_safety_and_monochrome_alpha(self):
  p=ROOT/'app/src/main/res/drawable-nodpi';a=Image.open(p/'tc_ds1_launcher_foreground.png').convert('RGBA');m=Image.open(p/'tc_ds1_launcher_monochrome.png').convert('RGBA');self.assertEqual(a.getchannel('A').tobytes(),m.getchannel('A').tobytes())
  for y in range(a.height):
   for x in range(a.width):
    if a.getpixel((x,y))[3]:self.assertLessEqual(math.hypot(x-a.width/2,y-a.height/2),a.width*33/108)
 def test_brand_files_have_not_drifted(self):
  b=ROOT/'design-system/brand';j=json.loads((b/'manifest.json').read_text())
  for name,sha in j['outputs'].items():self.assertEqual(hashlib.sha256((ROOT/name).read_bytes()).hexdigest(),sha,name)
 def test_shapes_regenerate_without_reconstruction(self):
  subprocess.run([sys.executable,str(ROOT/'design-system/tools/brand_assets.py'),'--check'],check=True,capture_output=True)
if __name__=='__main__':unittest.main()
