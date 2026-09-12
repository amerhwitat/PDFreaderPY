"""Small local-first Chimera P2P reference transport for document metadata.

Only metadata/state envelopes are synchronized. Document bytes and credentials
are never transmitted implicitly, and no remote command execution is exposed.
"""
from __future__ import annotations
import asyncio, hashlib, json, time

class ChimeraState:
    def __init__(self, **values): self.values = values
    def to_dict(self): return {"schema":"chimera-128d/v1","values":self.values}

def frame(peer_id: str, sequence: int, kind: str, payload: dict, ttl: int = 60) -> bytes:
    body = {"protocol":"chimera-p2p/v1","peer_id":peer_id,"sequence":sequence,
            "kind":kind,"expires_at":time.time()+ttl,"payload":payload}
    body["payload_sha256"] = hashlib.sha256(json.dumps(payload, sort_keys=True, separators=(",", ":")).encode()).hexdigest()
    return (json.dumps(body, sort_keys=True, separators=(",", ":"))+"\n").encode()

class PeerGuard:
    def __init__(self): self.highest = {}
    def accept(self, peer_id, sequence):
        if sequence <= self.highest.get(peer_id, -1): return False
        self.highest[peer_id] = sequence; return True

async def serve(host="127.0.0.1", port=0, on_message=None):
    guard = PeerGuard()
    async def client(reader, writer):
        try:
            while line := await reader.readline():
                m=json.loads(line)
                if m.get("protocol") != "chimera-p2p/v1" or m.get("expires_at",0)<time.time(): continue
                if not guard.accept(m.get("peer_id",""), int(m.get("sequence",-1))): continue
                p=m.get("payload",{})
                if m.get("payload_sha256") != hashlib.sha256(json.dumps(p,sort_keys=True,separators=(",",":")).encode()).hexdigest(): continue
                if on_message: await on_message(m)
        finally:
            writer.close(); await writer.wait_closed()
    return await asyncio.start_server(client, host, port)
