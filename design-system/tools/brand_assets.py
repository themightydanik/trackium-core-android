#!/usr/bin/env python3
"""Lossless source crop + deterministic raster derivatives. No tracing/redrawing/AI."""
from pathlib import Path
from PIL import Image,ImageChops,ImageDraw
import hashlib,json,math,argparse,io
ROOT=Path(__file__).resolve().parents[2];B=ROOT/'design-system/brand'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def rendered():
 im=Image.open(B/'source/owner-wordmark.png').convert('RGBA')
 # Original transparent owner wordmark: first non-empty contiguous column run is the mark.
 a=im.getchannel('A');cols=[x for x in range(im.width) if a.crop((x,0,x+1,im.height)).getbbox()]
 end=cols[0]
 for x in cols[1:]:
  if x!=end+1:break
  end=x
 box=(cols[0],0,end+1,im.height);part=im.crop(box);bb=part.getbbox();box=(box[0]+bb[0],bb[1],box[0]+bb[2],bb[3]);mark=im.crop(box)
 assert box[2]<400 and mark.width>300 and mark.height>250,'Unexpected owner asset layout'
 out={B/'generated/mark.png':mark}
 def layer(size,mono=False,safe=.3055):
  alpha=mark.getchannel('A');cx=(mark.width-1)/2;cy=(mark.height-1)/2
  radius=max(math.hypot(x-cx,y-cy) for y in range(mark.height) for x in range(mark.width) if alpha.getpixel((x,y)))
  scale=size*safe*.94/radius;target=mark.resize((round(mark.width*scale),round(mark.height*scale)),Image.Resampling.LANCZOS)
  if mono:target=Image.merge('RGBA',(Image.new('L',target.size,255),)*3+(target.getchannel('A'),))
  canvas=Image.new('RGBA',(size,size));canvas.alpha_composite(target,((size-target.width)//2,(size-target.height)//2));return canvas
 fg=layer(432);mono=layer(432,True)
 out[ROOT/'app/src/main/res/drawable-nodpi/tc_ds1_launcher_foreground.png']=fg
 out[ROOT/'app/src/main/res/drawable-nodpi/tc_ds1_launcher_monochrome.png']=mono
 out[ROOT/'app/src/main/res/drawable-nodpi/tc_ds1_brand_mark.png']=mark
 for density,size in [('mdpi',24),('hdpi',36),('xhdpi',48),('xxhdpi',72),('xxxhdpi',96)]:out[ROOT/f'app/src/main/res/drawable-{density}/tc_ds1_notification_small.png']=layer(size,True,.43)
 for size in [32,48,180,192,512]:
  icon=Image.new('RGBA',(size,size),'#0B1220');icon.alpha_composite(layer(size,safe=.40));out[B/f'generated/app-{size}.png']=icon
 out[B/'generated/launcher-foreground.png']=fg;out[B/'generated/launcher-monochrome.png']=mono
 # A source/crop/mask contact sheet, not a redesigned logo.
 board=Image.new('RGB',(1200,660),'#0B1220');d=ImageDraw.Draw(board)
 d.text((24,15),'OWNER PNG / EXACT CROP / RASTER MASK STUDY - no reconstructed SVG',fill='#E8EEF6')
 for i,(name,img) in enumerate([('Original transparent wordmark',im),('Exact mark crop',mark),('Foreground: safe circle',fg),('Themed alpha',mono)]):
  panel=Image.new('RGBA',(270,210),'#132238');thumb=img.copy();thumb.thumbnail((250,170),Image.Resampling.LANCZOS);panel.alpha_composite(thumb,((270-thumb.width)//2,20));board.paste(panel.convert('RGB'),(20+i*295,65));d.text((24+i*295,285),name,fill='#E8EEF6')
 for i,mask in enumerate(['circle','round','square']):
  tile=Image.new('RGBA',(180,180),'#132238');tile.alpha_composite(fg.resize((180,180),Image.Resampling.LANCZOS));m=Image.new('L',(180,180));draw=ImageDraw.Draw(m)
  if mask=='circle':draw.ellipse((0,0,179,179),fill=255)
  elif mask=='round':draw.rounded_rectangle((0,0,179,179),radius=44,fill=255)
  else:draw.rectangle((0,0,179,179),fill=255)
  board.paste(tile.convert('RGB'),(40+i*260,355),m);d.text((40+i*260,550),mask+' mask',fill='#E8EEF6')
 for i,size in enumerate([24,32,48]):
  tile=Image.new('RGBA',(size,size),'#132238');tile.alpha_composite(layer(size,safe=.40));board.paste(tile.convert('RGB'),(900,355+i*85));d.text((960,355+i*85),str(size)+' px',fill='#E8EEF6')
 out[ROOT/'docs/design-system-1/brand-comparison.png']=board.convert('RGBA')
 return out,box
if __name__=='__main__':
 parser=argparse.ArgumentParser();parser.add_argument('--check',action='store_true');args=parser.parse_args();out,box=rendered()
 for p,im in out.items():
  if args.check:
   assert p.exists(),p
   actual=Image.open(p).convert('RGBA');assert actual.size==im.size and actual.tobytes()==im.convert('RGBA').tobytes(),f'Pixel mismatch: {p}'
  else:p.parent.mkdir(parents=True,exist_ok=True);im.save(p,format='PNG',optimize=False)
 manifest={'source_repository':'themightydanik/trackium','source_commit':'ee384431f6e1d8c21240ee6cbcc1f9473b73555e','source_wordmark':'assets/img/logo.png','source_app_icon':'icon.png','wordmark_sha256':sha(B/'source/owner-wordmark.png'),'app_icon_sha256':sha(B/'source/owner-app-icon.png'),'crop_bbox':box,'method':'exact RGBA crop; alpha retained; Lanczos resizing; no inaccurate vector reconstruction','outputs':{str(p.relative_to(ROOT)):sha(p) for p in sorted(out)}}
 path=B/'manifest.json'
 if args.check:assert json.loads(path.read_text())==json.loads(json.dumps(manifest)),'Brand manifest mismatch'
 else:path.write_text(json.dumps(manifest,indent=2)+'\n')
 print('Owner source and generated brand pixels verified' if args.check else 'Owner raster brand assets generated')
