"""GUI-first desktop front end for PDFreaderPY ingestion utilities."""
from __future__ import annotations

import tkinter as tk
from pathlib import Path
from tkinter import filedialog, messagebox, ttk

import fitz
from PIL import Image, ImageTk


class PDFReaderApp(tk.Tk):
    def __init__(self) -> None:
        super().__init__()
        self.title("PDFreaderPY — Desktop GUI")
        self.geometry("980x720")
        self.path: Path | None = None
        self.page = 0
        self._photo = None
        self._build()

    def _build(self) -> None:
        top = ttk.Frame(self, padding=10)
        top.pack(fill="x")
        ttk.Button(top, text="Open PDF", command=self.open_pdf).pack(side="left")
        ttk.Button(top, text="Previous", command=lambda: self.change_page(-1)).pack(side="left", padx=5)
        ttk.Button(top, text="Next", command=lambda: self.change_page(1)).pack(side="left")
        self.info = ttk.Label(top, text="No document selected")
        self.info.pack(side="left", padx=12)
        self.preview = ttk.Label(self, anchor="center")
        self.preview.pack(fill="both", expand=True, padx=10, pady=10)

    def open_pdf(self) -> None:
        name = filedialog.askopenfilename(filetypes=(("PDF files", "*.pdf"), ("All files", "*.*")))
        if not name:
            return
        self.path = Path(name)
        self.page = 0
        self.render()

    def change_page(self, delta: int) -> None:
        if not self.path:
            return
        with fitz.open(self.path) as doc:
            self.page = max(0, min(self.page + delta, len(doc) - 1))
        self.render()

    def render(self) -> None:
        if not self.path:
            return
        try:
            with fitz.open(self.path) as doc:
                if not doc:
                    return
                page = doc.load_page(self.page)
                pix = page.get_pixmap(matrix=fitz.Matrix(1.3, 1.3), alpha=False)
                image = Image.frombytes("RGB", (pix.width, pix.height), pix.samples)
            image.thumbnail((900, 600))
            self._photo = ImageTk.PhotoImage(image)
            self.preview.configure(image=self._photo)
            self.info.configure(text=f"{self.path.name} — page {self.page + 1}")
        except Exception as exc:
            messagebox.showerror("PDFreaderPY", str(exc))


if __name__ == "__main__":
    PDFReaderApp().mainloop()
