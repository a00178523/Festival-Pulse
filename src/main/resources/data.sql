-- =============================================
-- FESTIVALS
-- =============================================
INSERT INTO festival (id, name, description, start_date, end_date) VALUES
(1, 'Electric Picnic', 'Ireland''s premier arts and music festival held in Stradbally, Co. Laois', '2025-08-29', '2025-08-31'),
(2, 'Longitude', 'Urban music festival held in Marlay Park, Dublin', '2025-07-04', '2025-07-06'),
(3, 'Forbidden Fruit', 'Alternative music and arts festival in the Royal Hospital Kilmainham, Dublin', '2025-06-01', '2025-06-02'),
(4, 'Body & Soul', 'Boutique arts and music festival in Ballinlough Castle, Co. Westmeath', '2025-06-20', '2025-06-22');

-- =============================================
-- ELECTRIC PICNIC AREAS (festival_id = 1)
-- =============================================
INSERT INTO festival_area (id, festival_id, name, description, area_type) VALUES
(1,  1, 'Main Stage',         'The main outdoor stage headlined by top acts',         'Stage'),
(2,  1, 'Electric Arena',     'Large indoor arena for electronic and dance acts',      'Stage'),
(3,  1, 'Rankins Wood Stage', 'Intimate woodland stage for emerging artists',          'Stage'),
(4,  1, 'Food Village',       'Central food area with Irish and international cuisine','Food & Drink'),
(5,  1, 'Trailer Park',       'Street food trucks and casual dining area',             'Food & Drink'),
(6,  1, 'Craft Beer Garden',  'Local Irish craft beer and cider bar',                  'Food & Drink'),
(7,  1, 'Body & Mind',        'Wellness, yoga and mindfulness area',                   'Wellness'),
(8,  1, 'First Aid Point',    'Main medical and first aid station',                    'Medical'),
(9,  1, 'Campsite A',         'Main family campsite near the entrance',                'Campsite'),
(10, 1, 'Campsite B',         'General campsite on the east side of the grounds',      'Campsite');

-- =============================================
-- LONGITUDE AREAS (festival_id = 2)
-- =============================================
INSERT INTO festival_area (id, festival_id, name, description, area_type) VALUES
(11, 2, 'Main Stage',         'Main outdoor stage in Marlay Park',                    'Stage'),
(12, 2, 'Second Stage',       'Secondary stage for supporting acts',                  'Stage'),
(13, 2, 'Dance Tent',         'Covered tent for DJ sets and electronic acts',         'Stage'),
(14, 2, 'Food Court',         'Central food and drink area',                          'Food & Drink'),
(15, 2, 'Bar Village',        'Main bar area with multiple vendors',                  'Food & Drink'),
(16, 2, 'First Aid Point',    'Medical and first aid station',                        'Medical'),
(17, 2, 'Main Entrance',      'Primary festival entrance and ticketing',              'Entrance');

-- =============================================
-- FORBIDDEN FRUIT AREAS (festival_id = 3)
-- =============================================
INSERT INTO festival_area (id, festival_id, name, description, area_type) VALUES
(18, 3, 'Main Stage',         'Outdoor main stage at RHK grounds',                   'Stage'),
(19, 3, 'The Courtyard Stage','Intimate courtyard performance area',                  'Stage'),
(20, 3, 'The Greenhouse',     'Indoor stage for electronic and alternative acts',     'Stage'),
(21, 3, 'Street Food Market', 'Artisan food stalls and street food vendors',          'Food & Drink'),
(22, 3, 'Craft Bar',          'Craft beer and cocktail bar',                          'Food & Drink'),
(23, 3, 'First Aid Point',    'Medical and first aid station',                        'Medical');

-- =============================================
-- BODY & SOUL AREAS (festival_id = 4)
-- =============================================
INSERT INTO festival_area (id, festival_id, name, description, area_type) VALUES
(24, 4, 'Woodland Stage',     'Main stage set within the castle woodland',            'Stage'),
(25, 4, 'Castle Stage',       'Stage set against the backdrop of Ballinlough Castle', 'Stage'),
(26, 4, 'The Sanctuary',      'Wellness and healing arts area',                       'Wellness'),
(27, 4, 'The Lakeside',       'Lakeside bar and chill-out area',                      'Food & Drink'),
(28, 4, 'Food Forest',        'Organic and artisan food village',                     'Food & Drink'),
(29, 4, 'First Aid Point',    'Medical and first aid station',                        'Medical'),
(30, 4, 'Campsite',           'Main campsite on the castle grounds',                  'Campsite');
