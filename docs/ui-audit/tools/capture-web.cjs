const {chromium}=require('playwright');
const fs=require('fs');
const root=process.env.TRACKIUM_AUDIT_OUTPUT || require('path').resolve(__dirname,'../evidence');
const origin=process.env.TRACKIUM_AUDIT_ORIGIN || 'http://127.0.0.1:8765';
if(!['127.0.0.1','localhost'].includes(new URL(origin).hostname))throw new Error('Only a local disposable static origin is permitted');
(async()=>{
 const browser=await chromium.launch({executablePath:'/usr/bin/chromium',headless:true,args:['--no-sandbox']});
 const records=[];
 for(const [label,width,height] of [['desktop',1440,1000],['compact',320,640],['phone',390,844],['tablet',768,1024]]){
  const ctx=await browser.newContext({viewport:{width,height},reducedMotion:'reduce'});
  await ctx.route('**/*',r=>new URL(r.request().url()).origin===origin?r.continue():r.abort());
  const page=await ctx.newPage(); const errors=[];page.on('pageerror',e=>errors.push(e.message));
  await page.goto(origin+'/'); await page.waitForTimeout(600);
  if(['desktop','phone'].includes(label))await page.screenshot({path:`${root}/web-${label}-unpaired.png`,fullPage:true});
  await page.locator('#demo-toggle').click();await page.waitForTimeout(2200);
  for(const route of ['dashboard','shipments','devices','map','events','proofs','settings']){
   await page.evaluate(route=>location.hash=route,route);await page.waitForTimeout(120);
   const path=`web-${label}-${route}-preview.png`;
   await page.screenshot({path:`${root}/${path}`,fullPage:true});
   const measurements=await page.evaluate(()=>({horizontalOverflow:document.documentElement.scrollWidth-innerWidth,heading:document.querySelector('h1')?.innerText,preview:document.querySelector('#demo-banner')?.innerText,smallTargets:[...document.querySelectorAll('button,a,input')].filter(x=>{const r=x.getBoundingClientRect();return r.width>0&&r.height>0&&r.height<44}).length}));
   records.push({path,viewport:{width,height},mode:'explicit static preview; unpaired; no backend',...measurements,errors:[...errors]});
  }
  if(label==='phone'){
   await page.evaluate(()=>location.hash='dashboard');await page.waitForTimeout(100);await page.locator('.shipment-row').first().click();await page.screenshot({path:`${root}/web-phone-shipment-dialog-preview.png`,fullPage:true});await page.keyboard.press('Escape');
   await page.addStyleTag({content:'html {font-size:200% !important}'});await page.screenshot({path:`${root}/web-phone-font200-preview.png`,fullPage:true});
  }
  await ctx.close();
 }
 fs.writeFileSync(`${root}/web-capture-metadata.json`,JSON.stringify({sourceCommit:process.env.TRACKIUM_WEB_COMMIT || 'NOT PROVIDED: set TRACKIUM_WEB_COMMIT to the checkout SHA',runtime:'Chromium headless, browser emulated viewports; not physical devices',records},null,2));
 await browser.close();console.log('Captured',records.length,'route/viewport combinations');
})().catch(e=>{console.error(e);process.exit(1)});
