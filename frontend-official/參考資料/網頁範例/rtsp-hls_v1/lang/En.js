// English language pack
const en = {
    meta: {
        title: 'RTSPCloud - Taiwan\'s Best Value Streaming Solution',
        description: 'Professional RTSPCloud streaming service at 1/10 the price of international providers. Enterprise on-premise deployment and cloud hosting options, 24h/7d stable operation, serving 18+ enterprise clients with 74+ streams.'
    },
    nav: {
        streaming: 'Streaming',
        cases: 'Success Cases',
        how: 'How It Works',
        plans: 'Pricing',
        contact: 'Contact',
        menu: 'Menu'
    },
    hero: {
        title: 'RTSP to HLS Streaming Service',
        subtitle: 'Convert your existing RTSP cameras to globally compatible HLS web streaming',
        cta: 'View Plans from NT$ 5,000'
    },
    comparison: {
        title: 'How Much Do Global Services Cost Per Year?',
        subtitle: 'International providers\' pricing (excluding hosting and bandwidth)',
        wowza: {
            priceNote: 'Annual License Fee',
            features: [
                'Per-stream pricing',
                'Industry standard software',
                'Bring your own server',
                'Bandwidth not included',
                'DRM support'
            ]
        },
        flussonic: {
            priceNote: 'Annual Subscription',
            features: [
                'Per-stream pricing',
                'European provider',
                'Bring your own server',
                'Bandwidth not included',
                'Enterprise features'
            ]
        }
    },
    solution: {
        badge: 'Best Value',
        title: 'In Taiwan, You Only Need...',
        savings: 'Save 90% on costs!',
        description: 'First year (including installation) only <strong>NT$ 5,000</strong>, annual maintenance just <strong>NT$ 4,500</strong><br>Same streaming functionality, only <strong>1/10 the price</strong> of international providers'
    },
    cases: {
        title: 'Success Stories',
        subtitle: 'We provide stable 24h/7d streaming services for hundreds of cameras across Taiwan',
        stats: [
            { label: 'Active Streams', desc: 'Running Stable' },
            { label: 'Enterprise Clients', desc: 'Long-term Partners' },
            { label: 'Uptime', desc: 'Year-round' },
            { label: 'Reliability', desc: 'Continuous Monitoring' }
        ],
        examples: {
            title: 'Use Cases',
            items: [
                { title: 'Corporate Monitoring', desc: 'Multi-site synchronized viewing, enhanced management efficiency' },
                { title: 'Environmental Monitoring', desc: 'Real-time river and water level monitoring systems' },
                { title: 'Factory Production', desc: 'Remote monitoring and management of production lines' },
                { title: 'Construction Safety', desc: '24-hour construction site monitoring' }
            ]
        },
        customerNote: 'We serve 18+ enterprise clients including <strong>MRT Circular Line Monitoring, Tamsui-Bali Expressway Traffic Monitoring, Hsinta Power Plant Surveillance</strong><br>providing reliable streaming conversion services',
        demo: {
            title: 'Live Demo Streams',
            intro: 'Click below to watch actual live streaming videos',
            cards: [
                { tag: 'Major Infrastructure', title: 'Tamsui Bridge', button: 'Watch Live' },
                { tag: 'Transportation', title: 'MRT Circular Line', button: 'Watch Live' },
                { tag: 'Road Monitoring', title: 'Tamsui-Bali Expressway', button: 'Watch Live' },
                { tag: 'Power Facilities', title: 'Hsinta Monitoring', button: 'Watch Live' },
                { tag: 'Port Facilities', title: 'Datan Monitoring System', button: 'Watch Live' },
                { tag: 'Engineering', title: 'Sanmin-Yuli Section', button: 'Watch Live' }
            ],
            note1: 'These are actual operating monitoring systems',
            note2: 'Using RTSP to HLS conversion service, 24-hour stable streaming'
        }
    },
    how: {
        title: 'How It Works',
        subtitle: 'Three steps to get your video online',
        steps: [
            {
                step: 'STEP 1',
                title: 'Provide RTSP Source',
                desc: 'Simply provide a connectable RTSP URL (rtsp://...), no need to change existing equipment.'
            },
            {
                step: 'STEP 2',
                title: 'We Convert It',
                desc: 'Our streaming server (on-premise or cloud) pulls the video in real-time and converts it to HLS format.'
            },
            {
                step: 'STEP 3',
                title: 'Global Web Playback',
                desc: 'You\'ll receive an .m3u8 URL that can be embedded in any browser, app, or player.'
            }
        ]
    },
    plans: {
        title: 'Choose Your Best Plan',
        subtitle: 'We have solutions for both on-premise deployment and cloud hosting',
        plan1: {
            badge: 'Most Popular',
            name: 'Enterprise On-Premise',
            type: 'On-Premise｜Bring Your Server',
            unit: '/ per stream/year',
            features: [
                'Client provides server (physical or cloud)',
                'We handle installation and configuration',
                'Passthrough transcoding',
                'Annual maintenance NT$ 4,500',
                'Suitable for enterprises with IT staff',
                'Full control over servers and data'
            ],
            cta: 'Contact Now'
        },
        plan2: {
            badge: 'Cloud Solution',
            name: 'Cloud Hosting (Basic)',
            type: 'SaaS｜We Handle Everything',
            unit: '/ per stream/month',
            features: [
                'All-in-one service, no server needed',
                'Includes 100 GB monthly bandwidth',
                'Passthrough transcoding',
                'Excess bandwidth NT$ 4/GB',
                'Suitable for SMBs and developers',
                'Quick launch, ready to use'
            ],
            cta: 'Contact Now'
        },
        plan3: {
            badge: 'Cloud Solution',
            name: 'Cloud Hosting (Professional)',
            type: 'SaaS｜Multi-Quality Transcoding',
            unit: '/ per stream/month',
            features: [
                'All-in-one service, no server needed',
                'Includes 500 GB monthly bandwidth',
                'ABR multi-quality transcoding',
                'Excess bandwidth NT$ 3/GB',
                'Suitable for professional broadcast, high-traffic events',
                'Automatic quality adjustment, better viewing experience'
            ],
            cta: 'Inquire Now'
        }
    },
    whyUs: {
        title: 'Why Choose Us?',
        subtitle: 'Focus on core features, save unnecessary expenses',
        benefits: [
            {
                title: 'Exceptional Value',
                desc: 'At 1/10 the price of international solutions, enjoy the same level of streaming service. We focus on core functionality, eliminating unnecessary premiums.'
            },
            {
                title: 'Local Support',
                desc: 'Taiwan-based team, Chinese communication, local support. No waiting for email responses across time zones - reach us anytime.'
            },
            {
                title: 'Rapid Deployment',
                desc: 'Professional team handles all technical details. From installation to launch, setup can be completed in as little as one day.'
            },
            {
                title: 'Ongoing Maintenance',
                desc: 'More than just software sales - we provide long-term maintenance support ensuring 24h/7d system stability.'
            },
            {
                title: 'Rich Experience',
                desc: 'Team with 8+ years of development experience. System has been running stably for 7 years - a time-tested reliable solution.'
            }
        ]
    },
    contact: {
        title: 'Ready to Get Started?',
        subtitle: 'Contact us now for exclusive quotes and free technical consultation',
        button: 'Contact Us',
        info: {
            email: 'Email',
            phone: 'Phone',
            address: 'Address',
            addressDetail: '1F., No. 16, Lane 80, Yongping St., Shilin Dist., Taipei City, Taiwan'
        },
        form: {
            title: 'Fill Out the Form to Contact Us',
            name: 'Name *',
            namePlaceholder: 'Please enter your name',
            email: 'Email *',
            emailPlaceholder: 'example@email.com',
            phone: 'Phone',
            phonePlaceholder: '+886-912-345-678',
            company: 'Company Name',
            companyPlaceholder: 'Please enter your company name',
            message: 'Message *',
            messagePlaceholder: 'Please tell us your needs or questions',
            submit: 'Send Message',
            sending: 'Sending...',
            success: '✓ Message sent successfully! We will contact you soon.',
            error: '✗ Failed to send. Please email us directly at service.ginting@gmail.com or call 02-88113938'
        }
    },
    footer: {
        company: 'Shenyuan International Co., Ltd.',
        address: '1F., No. 16, Lane 80, Yongping St., Shilin Dist., Taipei City 111, Taiwan (R.O.C.)',
        service: 'RTSPCloud Streaming Service',
        tagline: 'Professional streaming technology at local prices',
        contactTitle: 'Contact Information',
        quickLinks: 'Quick Links',
        copyright: '© 2025 Shenyuan International Co., Ltd. All rights reserved.',
        backToTop: 'Back to Top'
    }
};
