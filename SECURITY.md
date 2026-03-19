# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.0.x   | ✓ Yes     |

## Reporting a Vulnerability

Please **do not** open a public GitHub issue for security vulnerabilities.

Report vulnerabilities privately via [GitHub's private vulnerability reporting](https://github.com/clydegerber/i18n-fx/security/advisories/new).

You can expect:
- **Acknowledgement** within 5 business days
- **Status update** within 15 business days
- A fix or mitigation plan communicated before any public disclosure

## Security Considerations

`i18n-fx` delegates all UI updates to the JavaFX Application Thread. Resource bundle content is applied directly to component properties — ensure that resource bundle files in your application are sourced from trusted locations only.
