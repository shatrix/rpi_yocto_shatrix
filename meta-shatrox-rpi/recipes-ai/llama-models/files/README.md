# LLM Models for Raspberry Pi 5

This directory contains pre-quantized Large Language Models optimized for the Raspberry Pi 5 with 4GB RAM.

## Installed Models

### Qwen2.5-1.5B-Instruct (Q4_K_M)
- **Size**: ~950MB
- **Parameters**: 1.5 billion
- **Quantization**: Q4_K_M (4-bit, medium quality)
- **Performance**: ~9-10 tokens/second on Raspberry Pi 5
- **RAM Usage**: ~1.2GB during inference
- **Context**: Up to 4096 tokens

**Model Source**: [bartowski/Qwen2.5-1.5B-Instruct-GGUF](https://huggingface.co/bartowski/Qwen2.5-1.5B-Instruct-GGUF)

**Original Model**: [Qwen/Qwen2.5-1.5B-Instruct](https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct)

## Usage

### Quick Start (CLI)
```bash
llama-quick-start "Your prompt here"
```

### Manual Invocation
```bash
llama-cli -m /usr/share/llama-models/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf \
    -p "What is Raspberry Pi?" \
    --ctx-size 2048 \
    --n-predict 256
```

### Server Mode (HTTP API)
```bash
# Start server
llama-server-start

# Query via API
curl http://localhost:8080/v1/chat/completions \
    -H "Content-Type: application/json" \
    -d '{
        "messages": [
            {"role": "user", "content": "Hello!"}
        ],
        "max_tokens": 100
    }'
```

## Model Information

The default symlink points to `Qwen2.5-1.5B-Instruct-Q4_K_M.gguf` for convenience.

### What is Q4_K_M Quantization?
- **Q4**: 4-bit quantization (weights compressed to 4 bits)
- **K_M**: Medium quality variant of the K-quant method
- **Benefits**: Significantly smaller size, faster inference, minimal quality loss
- **Trade-offs**: Slight accuracy reduction compared to full precision

### Recommended Settings for 4GB RAM
- **Context Size**: 2048 tokens (can go up to 4096 if needed)
- **Batch Size**: 512
- ** Threads**: 4 (for Raspberry Pi 5's quad-core CPU)
- **No mmap**: Use `--no-mmap` flag for better performance on SD cards

## License

Qwen2.5 models are released under the Apache 2.0 license.
See: https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct
