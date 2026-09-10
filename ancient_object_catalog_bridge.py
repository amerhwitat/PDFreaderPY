"""PDF-to-Ancient-Object catalog bridge.

Renders a selected PDF page and creates a provenance-preserving object record
compatible with amerhwitat/nlp/ancient_objects_db.py.
"""
from __future__ import annotations
import argparse, json
from pathlib import Path

def render_page(pdf_path:Path,page_number:int,output:Path)->Path:
    import fitz
    doc=fitz.open(pdf_path); page=doc[page_number-1]; pix=page.get_pixmap(matrix=fitz.Matrix(2,2),alpha=False); output.parent.mkdir(parents=True,exist_ok=True); pix.save(output); return output

def make_record(pdf_path,page_number,image_path,**metadata):
    record={'title':metadata.get('title') or f'{Path(pdf_path).stem} page {page_number}','object_type':metadata.get('object_type','inscription'),'period_key':metadata.get('period_key',''),'source_name':metadata.get('source_name','PDFreaderPY'),'source_url':metadata.get('source_url',''),'image_local_path':str(image_path),'provenance':f'PDF: {Path(pdf_path).name}; page: {page_number}'}
    record.update({k:v for k,v in metadata.items() if v not in (None,'')}); return record

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('pdf',type=Path); ap.add_argument('--page',type=int,default=1); ap.add_argument('--output',type=Path,default=Path('ancient_object_record.json')); ap.add_argument('--period',dest='period_key',default=''); ap.add_argument('--title',default=''); args=ap.parse_args()
    image=render_page(args.pdf,args.page,args.output.with_suffix('.png')); record=make_record(args.pdf,args.page,image,title=args.title,period_key=args.period_key); args.output.write_text(json.dumps(record,ensure_ascii=False,indent=2),encoding='utf-8'); print(args.output)
if __name__=='__main__': main()
